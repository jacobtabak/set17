package dev.set17.earlygame

import dev.set17.earlygame.model.CompRecommendation
import dev.set17.tftacademy.api.TftAcademyRepository
import dev.set17.tftacademy.model.Comp
import dev.set17.tftacademy.model.Tier

class EarlyGameEngine(private val repo: TftAcademyRepository) {

    /** S/A comps for recommendations. */
    private var recommendableComps: List<Comp> = emptyList()
    /** S/A/B comps for flexibility scoring. */
    private var scoringComps: List<Comp> = emptyList()
    private var earlyChampionNames: List<String> = emptyList()

    fun init() {
        earlyChampionNames = repo.getChampionNamesForRole("early")
        val allSummaries = repo.getAllComps()
        val sabSummaries = allSummaries.filter { it.tier in setOf(Tier.S, Tier.A, Tier.B) }
        scoringComps = sabSummaries.mapNotNull { repo.getComp(it.compSlug) }
        recommendableComps = scoringComps
    }

    fun getEarlyChampionPool(): List<String> = earlyChampionNames

    /** Score each early champion by how many S/A/B comps use them. */
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

    /** Given selected champions, recommend S/A comps ranked by match quality. */
    fun recommend(selectedChampions: Set<String>): List<CompRecommendation> {
        if (selectedChampions.isEmpty()) return emptyList()

        return recommendableComps.mapNotNull { comp ->
            val earlyNames = comp.earlyComp.map { it.apiName }.toSet()
            val matched = selectedChampions.intersect(earlyNames).toList().sorted()
            if (matched.isEmpty()) return@mapNotNull null

            val missing = (earlyNames - selectedChampions).toList().sorted()
            val score = matched.size * tierWeight(comp.tier)

            CompRecommendation(
                comp = comp.toSummary(),
                matchedEarlyChampions = matched,
                missingEarlyChampions = missing,
                score = score,
            )
        }.sortedWith(compareByDescending<CompRecommendation> { it.score }.thenBy { it.comp.title })
    }

    fun getFullComp(slug: String): Comp? = repo.getComp(slug)

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
