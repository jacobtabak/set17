package dev.set17.tftacademy.model

data class Comp(
    val id: String,
    val title: String,
    val tier: Tier,
    val style: String,
    val difficulty: Difficulty,
    val compSlug: String,
    val set: Int,
    val created: String,
    val updated: String,
    val augmentsTip: String,
    val mainChampion: MainChampion,
    val earlyComp: List<Champion>,
    val finalComp: List<Champion>,
    val altBuilds: List<Champion>,
    val maxCap: List<MaxCapChampion>,
    val augments: List<Augment>,
    val augmentTypes: List<String>,
    val carousel: List<String>,
    val tips: List<Tip>,
)

data class CompSummary(
    val id: String,
    val title: String,
    val tier: Tier,
    val style: String,
    val difficulty: Difficulty,
    val compSlug: String,
    val mainChampionApiName: String,
)

enum class Tier { S, A, B, C, X }

enum class Difficulty { EASY, MEDIUM, HARD, CONDITIONAL }

data class MainChampion(
    val apiName: String,
    val cost: Int,
)

data class Champion(
    val apiName: String,
    val stars: Int = 1,
    val boardIndex: Int? = null,
    val items: List<String> = emptyList(),
)

data class MaxCapChampion(
    val apiName: String,
    val stars: Int = 1,
    val items: List<String> = emptyList(),
    val predecessors: List<String> = emptyList(),
)

data class Augment(
    val apiName: String,
    val disabled: Boolean = false,
)

data class Tip(
    val stage: String,
    val tip: String,
)
