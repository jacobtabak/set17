package dev.set17.earlygame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.set17.tftacademy.champion.ChampionData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChampionSelectorGrid(
    champions: List<String>,
    selectedChampions: Set<String>,
    championScores: Map<String, Int>,
    filter: String,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pool = if (filter.isBlank()) {
        val withScore = champions.filter { (championScores[it] ?: 0) > 0 }
        val selected = selectedChampions.filter { it !in withScore }
        withScore + selected
    } else {
        val query = filter.lowercase()
        ChampionData.champions.keys.filter {
            it.removePrefix("TFT17_").removePrefix("TFT_").lowercase().contains(query)
        }
    }

    val sorted = pool.sortedByDescending { championScores[it] ?: 0 }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for (champ in sorted) {
            val score = championScores[champ] ?: 0
            val cost = ChampionData.champions[champ]?.cost ?: 1
            ChampionChip(
                name = champ.removePrefix("TFT17_").removePrefix("TFT_"),
                selected = champ in selectedChampions,
                score = score,
                costColor = TftColors.costColor(cost),
                onClick = { onToggle(champ) },
            )
        }
    }
}
