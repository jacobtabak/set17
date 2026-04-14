package dev.set17.earlygame.ui

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import dev.set17.tftacademy.item.ItemComponentMap

@Composable
fun ItemIcon(
    apiName: String,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = "https://assets.tftacademy.com/items/$apiName.webp",
        contentDescription = ItemComponentMap.displayName(apiName),
        modifier = modifier.aspectRatio(1f).clip(RoundedCornerShape(4.dp)),
    )
}
