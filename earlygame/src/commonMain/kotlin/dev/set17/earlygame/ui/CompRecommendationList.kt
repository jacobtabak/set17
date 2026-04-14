package dev.set17.earlygame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.set17.earlygame.model.CompRecommendation

@Composable
fun CompRecommendationList(
    recommendations: List<CompRecommendation>,
    selectedSlug: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(recommendations) {
        listState.scrollToItem(0)
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(recommendations, key = { it.comp.compSlug }) { rec ->
            CompRecommendationCard(
                recommendation = rec,
                isSelected = rec.comp.compSlug == selectedSlug,
                onClick = { onSelect(rec.comp.compSlug) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
