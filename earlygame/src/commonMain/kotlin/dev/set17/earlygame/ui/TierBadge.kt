package dev.set17.earlygame.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.set17.tftacademy.model.Tier

@Composable
fun TierBadge(tier: Tier, modifier: Modifier = Modifier) {
    val color = TftColors.tierColor(tier)
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .widthIn(min = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = tier.name,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
