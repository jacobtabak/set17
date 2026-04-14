package dev.set17.earlygame

import dev.set17.earlygame.model.CompRecommendation
import dev.set17.tftacademy.api.TftAcademyRepository
import dev.set17.tftacademy.item.ItemComponentMap
import dev.set17.tftacademy.champion.ChampionData
import dev.set17.tftacademy.model.Champion
import dev.set17.tftacademy.model.Comp
import dev.set17.tftacademy.model.Tier

class EarlyGameEngine(
    private val repo: TftAcademyRepository,
    private val config: ScoringConfig = ScoringConfig(),
) {
    private var recommendableComps: List<Comp> = emptyList()
    private var scoringComps: List<Comp> = emptyList()
    private var earlyChampionNames: List<String> = emptyList()

    fun init() {
        earlyChampionNames = repo.getChampionNamesForRole("early")
            .filter { it in ChampionData.champions }
        val allSummaries = repo.getAllComps()
        val sabSummaries = allSummaries.filter { it.tier in setOf(Tier.S, Tier.A, Tier.B) }
        scoringComps = sabSummaries.mapNotNull { repo.getComp(it.compSlug) }
        recommendableComps = scoringComps
    }

    fun getEarlyChampionPool(): List<String> = earlyChampionNames

    fun scoreChampions(): Map<String, Int> {
        val scores = mutableMapOf<String, Int>()
        for (comp in scoringComps) {
            val weight = tierWeight(comp.tier)
            for (champ in comp.earlyComp) {
                scores[champ.apiName] = (scores[champ.apiName] ?: 0) + weight
            }
        }
        return scores
    }

    fun scoreComponents(): Map<String, Int> {
        val scores = mutableMapOf<String, Int>()
        for (comp in scoringComps) {
            val weight = tierWeight(comp.tier)
            for (item in carryItems(comp)) {
                val recipe = ItemComponentMap.componentsOf(item) ?: continue
                scores[recipe.first] = (scores[recipe.first] ?: 0) + weight
                if (recipe.first != recipe.second) {
                    scores[recipe.second] = (scores[recipe.second] ?: 0) + weight
                }
            }
        }
        return scores
    }

    fun recommend(
        selectedChampions: Set<String>,
        componentCounts: Map<String, Int> = emptyMap(),
    ): List<CompRecommendation> {
        if (selectedChampions.isEmpty() && componentCounts.isEmpty()) return emptyList()

        return recommendableComps.mapNotNull { comp ->
            val tw = tierWeight(comp.tier)
            val earlyNames = comp.earlyComp.map { it.apiName }.toSet()
            val matched = selectedChampions.intersect(earlyNames).toList().sorted()
            val missing = (earlyNames - selectedChampions).toList().sorted()
            val championScore = matched.size * config.championMatchWeight * tw

            val craftable = mutableListOf<String>()
            val partial = mutableListOf<String>()
            var itemScore = 0

            if (componentCounts.isNotEmpty()) {
                val pool = componentCounts.toMutableMap()
                val items = carryItems(comp)
                val tank = findTank(comp)
                val tankItems = tank?.items?.filter { ItemComponentMap.componentsOf(it) != null } ?: emptyList()

                // Phase 1: carry full matches (priority ordered — items 1&2 then 3)
                for ((i, item) in items.withIndex()) {
                    if (canCraftFull(item, pool)) {
                        craftable.add(item)
                        consumeRecipe(item, pool)
                        val w = if (i < 2) config.carryItemWeight else config.carryItem3Weight
                        itemScore += w * tw
                    }
                }

                // Phase 2: tank full matches
                for (item in tankItems) {
                    if (canCraftFull(item, pool)) {
                        craftable.add(item)
                        consumeRecipe(item, pool)
                        itemScore += config.tankItemWeight * tw
                    }
                }

                // Phase 3: carry partial matches
                for ((i, item) in items.withIndex()) {
                    if (item in craftable) continue
                    val component = findPartialComponent(item, pool)
                    if (component != null) {
                        partial.add(item)
                        decrement(pool, component)
                        val w = if (i < 2) config.partialItemWeight else config.partialItem3Weight
                        itemScore += w * tw
                    }
                }

                // Phase 4: tank partial matches
                for (item in tankItems) {
                    if (item in craftable) continue
                    val component = findPartialComponent(item, pool)
                    if (component != null) {
                        partial.add(item)
                        decrement(pool, component)
                        itemScore += config.tankPartialWeight * tw
                    }
                }

                // Phase 5: carousel matches
                for (entry in comp.carousel) {
                    if ((pool[entry] ?: 0) > 0) {
                        decrement(pool, entry)
                        itemScore += config.carouselMatchWeight * tw
                    }
                }
            }

            val totalScore = championScore + itemScore
            if (totalScore == 0) return@mapNotNull null

            CompRecommendation(
                comp = comp.toSummary(),
                matchedEarlyChampions = matched,
                missingEarlyChampions = missing,
                score = totalScore,
                craftableCarryItems = craftable,
                partialCarryItems = partial,
                itemScore = itemScore,
            )
        }.sortedWith(compareByDescending<CompRecommendation> { it.score }.thenBy { it.comp.title })
    }

    /** All S/A/B comps as recommendations with no match data, sorted by tier. */
    fun allComps(): List<CompRecommendation> {
        return recommendableComps.map { comp ->
            CompRecommendation(
                comp = comp.toSummary(),
                matchedEarlyChampions = emptyList(),
                missingEarlyChampions = emptyList(),
                score = 0,
            )
        }.sortedWith(compareBy<CompRecommendation> { it.comp.tier.ordinal }.thenBy { it.comp.title })
    }

    fun getFullComp(slug: String): Comp? = repo.getComp(slug)

    private fun carryItems(comp: Comp): List<String> {
        val carryApiName = comp.mainChampion.apiName
        return comp.finalComp
            .firstOrNull { it.apiName == carryApiName }
            ?.items
            ?.filter { ItemComponentMap.componentsOf(it) != null }
            ?: emptyList()
    }

    /** Find the tank: non-carry champion with the most defensive items (minimum 2). */
    private fun findTank(comp: Comp): Champion? {
        val carryApiName = comp.mainChampion.apiName
        return comp.finalComp
            .filter { it.apiName != carryApiName }
            .maxByOrNull { champ -> champ.items.count { ItemComponentMap.isDefensiveItem(it) } }
            ?.takeIf { champ -> champ.items.count { ItemComponentMap.isDefensiveItem(it) } >= 2 }
    }

    private fun canCraftFull(itemApiName: String, pool: Map<String, Int>): Boolean {
        val recipe = ItemComponentMap.componentsOf(itemApiName) ?: return false
        return if (recipe.first == recipe.second) {
            (pool[recipe.first] ?: 0) >= 2
        } else {
            (pool[recipe.first] ?: 0) >= 1 && (pool[recipe.second] ?: 0) >= 1
        }
    }

    private fun consumeRecipe(itemApiName: String, pool: MutableMap<String, Int>) {
        val recipe = ItemComponentMap.componentsOf(itemApiName) ?: return
        decrement(pool, recipe.first)
        decrement(pool, recipe.second)
    }

    private fun findPartialComponent(itemApiName: String, pool: Map<String, Int>): String? {
        val recipe = ItemComponentMap.componentsOf(itemApiName) ?: return null
        return when {
            (pool[recipe.first] ?: 0) >= 1 -> recipe.first
            (pool[recipe.second] ?: 0) >= 1 -> recipe.second
            else -> null
        }
    }

    private fun decrement(pool: MutableMap<String, Int>, key: String) {
        pool[key] = (pool[key] ?: 1) - 1
        if (pool[key]!! <= 0) pool.remove(key)
    }

    private fun tierWeight(tier: Tier): Int = when (tier) {
        Tier.S -> 3
        Tier.A -> 2
        Tier.B -> 1
        else -> 0
    }

    private fun Comp.toSummary() = dev.set17.tftacademy.model.CompSummary(
        id = id,
        title = title,
        tier = tier,
        style = style,
        difficulty = difficulty,
        compSlug = compSlug,
        mainChampionApiName = mainChampion.apiName,
    )
}
