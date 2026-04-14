package dev.set17.earlygame.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChampionChip(
    name: String,
    selected: Boolean,
    score: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) TftColors.chipSelected else TftColors.chipDefault
    val border = if (selected) TftColors.chipSelectedBorder else TftColors.chipDefaultBorder
    val textColor = if (selected) TftColors.textPrimary else TftColors.textSecondary

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = bg,
        border = BorderStroke(1.dp, border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "$score",
                color = if (selected) TftColors.textSecondary else TftColors.textMuted,
                fontSize = 11.sp,
            )
        }
    }
}
