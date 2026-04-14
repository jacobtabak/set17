package dev.set17.earlygame

import dev.set17.earlygame.model.CompRecommendation
import dev.set17.earlygame.model.GamePhase
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

    suspend fun init() {
        earlyChampionNames = repo.getChampionNamesForRole("early")
            .filter { it in ChampionData.champions }
        val allSummaries = repo.getAllComps()
        val sabSummaries = allSummaries.filter { it.tier in setOf(Tier.S, Tier.A, Tier.B) }
        scoringComps = sabSummaries.mapNotNull { repo.getComp(it.compSlug) }
        recommendableComps = scoringComps
        unknownEmblems = findUnknownEmblems()
    }

    var unknownEmblems: List<String> = emptyList()
        private set

    private fun findUnknownEmblems(): List<String> {
        val unknown = mutableSetOf<String>()
        for (comp in recommendableComps) {
            for (champ in comp.finalComp) {
                for (item in champ.items) {
                    if (item.contains("Emblem", ignoreCase = true) &&
                        ItemComponentMap.componentsOf(item) == null
                    ) {
                        unknown.add(item)
                    }
                }
            }
        }
        return unknown.sorted().also {
            if (it.isNotEmpty()) {
                println("WARNING: Unknown emblem items found in comp data: $it")
                println("Add mappings to ItemComponentMap.emblemApiNameToTrait")
            }
        }
    }

    fun getEarlyChampionPool(): List<String> = earlyChampionNames

    /** Flex scores for early game champions (how many S/A/B comps use them in earlyComp). */
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

    /** Flex scores for late game champions (how many S/A/B comps use them in finalComp). */
    fun scoreLateChampions(): Map<String, Int> {
        val scores = mutableMapOf<String, Int>()
        for (comp in scoringComps) {
            val weight = tierWeight(comp.tier)
            for (champ in comp.finalComp) {
                if (champ.apiName in ChampionData.champions) {
                    scores[champ.apiName] = (scores[champ.apiName] ?: 0) + weight
                }
            }
        }
        return scores
    }

    /** Flex scores for completed items (how many S/A/B comps use them on any champion). */
    fun scoreItems(): Map<String, Int> {
        val scores = mutableMapOf<String, Int>()
        for (comp in scoringComps) {
            val weight = tierWeight(comp.tier)
            for (champ in comp.finalComp) {
                for (item in champ.items) {
                    scores[item] = (scores[item] ?: 0) + weight
                }
            }
        }
        return scores
    }

    fun scoreComponents(): Map<String, Int> {
        val scores = mutableMapOf<String, Int>()
        for (comp in scoringComps) {
            val weight = tierWeight(comp.tier)
            for (champ in comp.finalComp) {
                for (item in champ.items) {
                    val recipe = ItemComponentMap.componentsOf(item) ?: continue
                    scores[recipe.first] = (scores[recipe.first] ?: 0) + weight
                    if (recipe.first != recipe.second) {
                        scores[recipe.second] = (scores[recipe.second] ?: 0) + weight
                    }
                }
            }
        }
        return scores
    }

    fun recommend(
        selectedChampions: Set<String>,
        componentCounts: Map<String, Int> = emptyMap(),
        fullItemCounts: Map<String, Int> = emptyMap(),
        phase: GamePhase = GamePhase.EARLY,
    ): List<CompRecommendation> {
        if (selectedChampions.isEmpty() && componentCounts.isEmpty() && fullItemCounts.isEmpty()) {
            return emptyList()
        }

        return recommendableComps.mapNotNull { comp ->
            val tw = tierWeight(comp.tier)

            // Champion matching — source depends on phase
            val compChampNames = when (phase) {
                GamePhase.EARLY -> comp.earlyComp.map { it.apiName }.toSet()
                GamePhase.LATE -> comp.finalComp
                    .filter { it.apiName in ChampionData.champions }
                    .map { it.apiName }.toSet()
            }
            val matched = selectedChampions.intersect(compChampNames).toList().sorted()
            val missing = (compChampNames - selectedChampions).toList().sorted()
            val championScore = matched.size * config.championMatchWeight * tw

            val craftable = mutableListOf<String>()
            val partial = mutableListOf<String>()
            var itemScore = 0

            // Collect all comp items by role
            val carryItemList = carryItems(comp)
            val tank = findTank(comp)
            val tankItems = tank?.items?.filter { ItemComponentMap.componentsOf(it) != null } ?: emptyList()
            val (emblemItems, supportItems) = findSupportItems(comp, tank)
            val allCompItems = carryItemList + emblemItems + tankItems + supportItems

            // Phase 0: full item direct matches (user already has completed items)
            val itemPool = fullItemCounts.toMutableMap()
            if (itemPool.isNotEmpty()) {
                fun consumeItem(item: String): Boolean {
                    val c = itemPool[item] ?: return false
                    if (c <= 1) itemPool.remove(item) else itemPool[item] = c - 1
                    return true
                }
                // Check carry items first
                for ((i, item) in carryItemList.withIndex()) {
                    if (consumeItem(item)) {
                        craftable.add(item)
                        val w = if (i < 2) config.carryItemWeight else config.carryItem3Weight
                        itemScore += w * tw
                    }
                }
                // Emblems
                for (item in emblemItems) {
                    if (consumeItem(item)) {
                        craftable.add(item)
                        itemScore += config.emblemItemWeight * tw
                    }
                }
                // Tank
                for (item in tankItems) {
                    if (consumeItem(item)) {
                        craftable.add(item)
                        itemScore += config.tankItemWeight * tw
                    }
                }
                // Support
                for (item in supportItems) {
                    if (consumeItem(item)) {
                        craftable.add(item)
                        itemScore += config.supportItemWeight * tw
                    }
                }
            }

            // Component-based crafting (phases 1-9)
            if (componentCounts.isNotEmpty()) {
                val pool = componentCounts.toMutableMap()

                // Phase 1: carry full
                for ((i, item) in carryItemList.withIndex()) {
                    if (item in craftable) continue
                    if (canCraftFull(item, pool)) {
                        craftable.add(item)
                        consumeRecipe(item, pool)
                        val w = if (i < 2) config.carryItemWeight else config.carryItem3Weight
                        itemScore += w * tw
                    }
                }
                // Phase 2: emblem full
                for (item in emblemItems) {
                    if (item in craftable) continue
                    if (canCraftFull(item, pool)) {
                        craftable.add(item)
                        consumeRecipe(item, pool)
                        itemScore += config.emblemItemWeight * tw
                    }
                }
                // Phase 3: tank full
                for (item in tankItems) {
                    if (item in craftable) continue
                    if (canCraftFull(item, pool)) {
                        craftable.add(item)
                        consumeRecipe(item, pool)
                        itemScore += config.tankItemWeight * tw
                    }
                }
                // Phase 4: support full
                for (item in supportItems) {
                    if (item in craftable) continue
                    if (canCraftFull(item, pool)) {
                        craftable.add(item)
                        consumeRecipe(item, pool)
                        itemScore += config.supportItemWeight * tw
                    }
                }
                // Phase 5: carry partial
                for ((i, item) in carryItemList.withIndex()) {
                    if (item in craftable) continue
                    val component = findPartialComponent(item, pool)
                    if (component != null) {
                        partial.add(item)
                        decrement(pool, component)
                        val w = if (i < 2) config.partialItemWeight else config.partialItem3Weight
                        itemScore += w * tw
                    }
                }
                // Phase 6: emblem partial
                for (item in emblemItems) {
                    if (item in craftable) continue
                    val component = findPartialComponent(item, pool)
                    if (component != null) {
                        partial.add(item)
                        decrement(pool, component)
                        itemScore += config.emblemPartialWeight * tw
                    }
                }
                // Phase 7: tank partial
                for (item in tankItems) {
                    if (item in craftable) continue
                    val component = findPartialComponent(item, pool)
                    if (component != null) {
                        partial.add(item)
                        decrement(pool, component)
                        itemScore += config.tankPartialWeight * tw
                    }
                }
                // Phase 8: support partial
                for (item in supportItems) {
                    if (item in craftable) continue
                    val component = findPartialComponent(item, pool)
                    if (component != null) {
                        partial.add(item)
                        decrement(pool, component)
                        itemScore += config.supportPartialWeight * tw
                    }
                }
                // Phase 9: carousel
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

    suspend fun getFullComp(slug: String): Comp? = repo.getComp(slug)

    private fun carryItems(comp: Comp): List<String> {
        val carryApiName = comp.mainChampion.apiName
        return comp.finalComp
            .firstOrNull { it.apiName == carryApiName }
            ?.items
            ?.filter { ItemComponentMap.componentsOf(it) != null }
            ?: emptyList()
    }

    private fun findTank(comp: Comp): Champion? {
        val carryApiName = comp.mainChampion.apiName
        return comp.finalComp
            .filter { it.apiName != carryApiName }
            .maxByOrNull { champ -> champ.items.count { ItemComponentMap.isDefensiveItem(it) } }
            ?.takeIf { champ -> champ.items.count { ItemComponentMap.isDefensiveItem(it) } >= 2 }
    }

    private fun findSupportItems(comp: Comp, tank: Champion?): Pair<List<String>, List<String>> {
        val carryApiName = comp.mainChampion.apiName
        val tankApiName = tank?.apiName
        val emblems = mutableListOf<String>()
        val support = mutableListOf<String>()
        for (champ in comp.finalComp) {
            if (champ.apiName == carryApiName || champ.apiName == tankApiName) continue
            for (item in champ.items) {
                if (ItemComponentMap.componentsOf(item) == null) continue
                if (item in ItemComponentMap.emblemApiNameToTrait) {
                    emblems.add(item)
                } else {
                    support.add(item)
                }
            }
        }
        return emblems to support
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
