package dev.set17.earlygame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.set17.tftacademy.item.ItemComponentMap

private val ALL_ITEMS: List<String> by lazy {
    ItemComponentMap.recipes.keys.toList()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FullItemSelector(
    itemCounts: Map<String, Int>,
    itemScores: Map<String, Int>,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sorted = ALL_ITEMS.sortedByDescending { itemScores[it] ?: 0 }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for (apiName in sorted) {
            ComponentChip(
                apiName = apiName,
                count = itemCounts[apiName] ?: 0,
                onIncrement = { onIncrement(apiName) },
                onDecrement = { onDecrement(apiName) },
            )
        }
    }
}
