package dev.set17.earlygame.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.set17.earlygame.model.CompRecommendation

@Composable
fun CompRecommendationCard(
    recommendation: CompRecommendation,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val comp = recommendation.comp
    val border = if (isSelected) TftColors.chipSelectedBorder else TftColors.border

    Surface(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) TftColors.surfaceVariant else TftColors.surface,
        border = BorderStroke(1.dp, border),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TierBadge(comp.tier)
                Text(
                    text = comp.title,
                    color = TftColors.textPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = comp.style,
                    color = TftColors.textSecondary,
                    fontSize = 12.sp,
                )
                Text(
                    text = comp.difficulty.name,
                    color = TftColors.textMuted,
                    fontSize = 12.sp,
                )
            }

            if (recommendation.matchedEarlyChampions.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Have: ", color = TftColors.textSecondary, fontSize = 12.sp)
                    Text(
                        text = recommendation.matchedEarlyChampions.joinToString(", ") {
                            it.removePrefix("TFT17_")
                        },
                        color = TftColors.matchedChampion,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }

                if (recommendation.missingEarlyChampions.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Need: ", color = TftColors.textSecondary, fontSize = 12.sp)
                        Text(
                            text = recommendation.missingEarlyChampions.joinToString(", ") {
                                it.removePrefix("TFT17_")
                            },
                            color = TftColors.missingChampion,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            if (recommendation.craftableCarryItems.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Can craft:", color = TftColors.matchedChampion, fontSize = 12.sp)
                    for (item in recommendation.craftableCarryItems) {
                        ItemIcon(apiName = item, modifier = Modifier.height(18.dp))
                    }
                }
            }

            if (recommendation.partialCarryItems.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("1 away:", color = TftColors.missingChampion, fontSize = 12.sp)
                    for (item in recommendation.partialCarryItems) {
                        ItemIcon(apiName = item, modifier = Modifier.height(18.dp))
                    }
                }
            }
        }
    }
}
