package dev.set17.earlygame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.set17.earlygame.model.ChampionSort

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChampionSelectorGrid(
    champions: List<String>,
    selectedChampions: Set<String>,
    championScores: Map<String, Int>,
    sort: ChampionSort,
    filter: String,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filtered = if (filter.isBlank()) {
        champions
    } else {
        val query = filter.lowercase()
        champions.filter {
            it.removePrefix("TFT17_").removePrefix("TFT_").lowercase().contains(query)
        }
    }

    val sorted = when (sort) {
        ChampionSort.FLEX_RATING -> filtered.sortedByDescending { championScores[it] ?: 0 }
        ChampionSort.ALPHABETICAL -> filtered.sortedBy { it.removePrefix("TFT17_").removePrefix("TFT_") }
    }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for (champ in sorted) {
            val score = championScores[champ] ?: 0
            ChampionChip(
                name = champ.removePrefix("TFT17_").removePrefix("TFT_"),
                selected = champ in selectedChampions,
                score = score,
                onClick = { onToggle(champ) },
            )
        }
    }
}
