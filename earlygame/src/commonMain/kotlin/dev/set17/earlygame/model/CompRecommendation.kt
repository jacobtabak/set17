package dev.set17.earlygame.model

import dev.set17.tftacademy.model.CompSummary

data class CompRecommendation(
    val comp: CompSummary,
    val matchedEarlyChampions: List<String>,
    val missingEarlyChampions: List<String>,
    val score: Int,
)
