package dev.set17.earlygame.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val MAX_COUNT = 3

@Composable
fun ComponentChip(
    name: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = count > 0
    val bg = if (selected) TftColors.chipSelected else TftColors.chipDefault
    val border = if (selected) TftColors.chipSelectedBorder else TftColors.chipDefaultBorder
    val textColor = if (selected) TftColors.textPrimary else TftColors.textSecondary

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bg,
        border = BorderStroke(1.dp, border),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "\u2212",
                color = if (selected) TftColors.textSecondary else TftColors.chipDefaultBorder,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(enabled = selected, onClick = onDecrement)
                    .padding(horizontal = 10.dp),
            )
            Text(
                text = name,
                color = textColor,
                fontSize = 13.sp,
            )
            Text(
                text = "\u00d7$count",
                color = if (selected) TftColors.chipSelectedBorder else TftColors.textMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 2.dp),
            )
            Text(
                text = "+",
                color = if (count < MAX_COUNT) TftColors.textSecondary else TftColors.chipDefaultBorder,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable(enabled = count < MAX_COUNT, onClick = onIncrement)
                    .padding(horizontal = 10.dp),
            )
        }
    }
}
