package dev.set17.earlygame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val DISPLAY_COMPONENTS = listOf(
    "TFT_Item_BFSword" to "BF Sword",
    "TFT_Item_RecurveBow" to "Bow",
    "TFT_Item_NeedlesslyLargeRod" to "Rod",
    "TFT_Item_TearOfTheGoddess" to "Tear",
    "TFT_Item_ChainVest" to "Vest",
    "TFT_Item_NegatronCloak" to "Cloak",
    "TFT_Item_GiantsBelt" to "Belt",
    "TFT_Item_SparringGloves" to "Gloves",
    "TFT_Item_FryingPan" to "Pan",
    "TFT_Item_Spatula" to "Spatula",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComponentSelectorRow(
    componentCounts: Map<String, Int>,
    componentScores: Map<String, Int>,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sorted = DISPLAY_COMPONENTS.sortedByDescending { componentScores[it.first] ?: 0 }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        for ((apiName, displayName) in sorted) {
            ComponentChip(
                name = displayName,
                count = componentCounts[apiName] ?: 0,
                onIncrement = { onIncrement(apiName) },
                onDecrement = { onDecrement(apiName) },
            )
        }
    }
}
