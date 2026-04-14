package dev.set17.earlygame.model

import dev.set17.tftacademy.model.Comp

enum class GamePhase { EARLY, LATE }

data class EarlyGameState(
    val loading: Boolean = true,
    val error: String? = null,
    val selectedTab: GamePhase = GamePhase.EARLY,
    val earlyChampionPool: List<String> = emptyList(),
    val championScores: Map<String, Int> = emptyMap(),
    val lateChampionScores: Map<String, Int> = emptyMap(),
    val selectedChampions: Set<String> = emptySet(),
    val recommendations: List<CompRecommendation> = emptyList(),
    val selectedComp: Comp? = null,
    val filterText: String = "",
    val componentCounts: Map<String, Int> = emptyMap(),
    val componentScores: Map<String, Int> = emptyMap(),
    val fullItemCounts: Map<String, Int> = emptyMap(),
    val itemScores: Map<String, Int> = emptyMap(),
    val allComps: List<CompRecommendation> = emptyList(),
    val unknownEmblems: List<String> = emptyList(),
)
