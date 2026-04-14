package dev.set17.earlygame.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.set17.earlygame.EarlyGameEngine
import dev.set17.earlygame.model.EarlyGameState
import dev.set17.tftacademy.api.TftAcademyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val WIDE_BREAKPOINT = 600.dp

@Composable
fun EarlyGameScreen(
    repo: TftAcademyRepository,
    onNavigateToComp: (slug: String) -> Unit = {},
    restoreSlug: String? = null,
) {
    val engine = remember { EarlyGameEngine(repo) }
    var state by remember { mutableStateOf(EarlyGameState()) }

    LaunchedEffect(restoreSlug) {
        if (restoreSlug != null && state.selectedComp?.compSlug != restoreSlug) {
            val comp = engine.getFullComp(restoreSlug)
            if (comp != null) {
                state = state.copy(selectedComp = comp)
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                if (!repo.hasData()) {
                    repo.refresh()
                }
                engine.init()
            }
            state = state.copy(
                loading = false,
                earlyChampionPool = engine.getEarlyChampionPool(),
                championScores = engine.scoreChampions(),
                componentScores = engine.scoreComponents(),
                allComps = engine.allComps(),
            )
        } catch (e: Exception) {
            state = state.copy(loading = false, error = e.message)
        }
    }

    LaunchedEffect(state.selectedChampions, state.componentCounts) {
        val recs = engine.recommend(state.selectedChampions, state.componentCounts)
        state = state.copy(recommendations = recs)
    }

    Box(modifier = Modifier.fillMaxSize().background(TftColors.background)) {
        when {
            state.loading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(color = TftColors.tierS)
                    Text("Loading tier list...", color = TftColors.textSecondary, fontSize = 14.sp)
                }
            }
            state.error != null -> {
                Text(
                    text = "Error: ${state.error}",
                    color = TftColors.missingChampion,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
            }
            else -> {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWide = maxWidth >= WIDE_BREAKPOINT

                    LaunchedEffect(isWide) {
                        if (!isWide && state.selectedComp != null) {
                            onNavigateToComp(state.selectedComp!!.compSlug)
                        }
                    }

                    val toggleChampion = { champ: String ->
                        val new = if (champ in state.selectedChampions)
                            state.selectedChampions - champ
                        else
                            state.selectedChampions + champ
                        state = state.copy(selectedChampions = new, filterText = "")
                    }
                    val onFilterChanged = { text: String ->
                        state = state.copy(filterText = text)
                    }
                    val clearAll = {
                        state = state.copy(
                            selectedChampions = emptySet(),
                            componentCounts = emptyMap(),
                            filterText = "",
                            selectedComp = null,
                        )
                    }
                    val incrementComponent = { c: String ->
                        val cur = state.componentCounts[c] ?: 0
                        if (cur < 3) state = state.copy(componentCounts = state.componentCounts + (c to cur + 1))
                    }
                    val decrementComponent = { c: String ->
                        val cur = state.componentCounts[c] ?: 0
                        if (cur > 0) {
                            val new = if (cur == 1) state.componentCounts - c else state.componentCounts + (c to cur - 1)
                            state = state.copy(componentCounts = new)
                        }
                    }

                    if (isWide) {
                        WideLayout(
                            state = state,
                            engine = engine,
                            onToggleChampion = toggleChampion,
                            onIncrementComponent = incrementComponent,
                            onDecrementComponent = decrementComponent,
                            onFilterChanged = onFilterChanged,
                            onClearAll = clearAll,
                            onSelectComp = { slug ->
                                if (state.selectedComp?.compSlug == slug) {
                                    state = state.copy(selectedComp = null)
                                } else {
                                    val comp = engine.getFullComp(slug)
                                    state = state.copy(selectedComp = comp)
                                }
                            },
                            onCloseDetail = { state = state.copy(selectedComp = null) },
                        )
                    } else if (state.selectedComp == null) {
                        NarrowLayout(
                            state = state,
                            onToggleChampion = toggleChampion,
                            onIncrementComponent = incrementComponent,
                            onDecrementComponent = decrementComponent,
                            onFilterChanged = onFilterChanged,
                            onClearAll = clearAll,
                            onSelectComp = { slug -> onNavigateToComp(slug) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WideLayout(
    state: EarlyGameState,
    engine: EarlyGameEngine,
    onToggleChampion: (String) -> Unit,
    onIncrementComponent: (String) -> Unit,
    onDecrementComponent: (String) -> Unit,
    onFilterChanged: (String) -> Unit,
    onClearAll: () -> Unit,
    onSelectComp: (String) -> Unit,
    onCloseDetail: () -> Unit,
) {
    val detailWeight by animateFloatAsState(
        targetValue = if (state.selectedComp != null) 1f else 0f,
        animationSpec = tween(250),
    )

    Row(modifier = Modifier.fillMaxSize()) {
        ListColumn(
            state = state,
            onToggleChampion = onToggleChampion,
            onIncrementComponent = onIncrementComponent,
            onDecrementComponent = onDecrementComponent,
            onFilterChanged = onFilterChanged,
            onClearAll = onClearAll,
            onSelectComp = onSelectComp,
            modifier = Modifier.weight(1f).fillMaxHeight(),
        )

        if (detailWeight > 0f) {
            Box(
                modifier = Modifier
                    .weight(detailWeight)
                    .fillMaxHeight()
                    .background(TftColors.surface),
            ) {
                state.selectedComp?.let { comp ->
                    CompDetailPanel(comp, onBack = onCloseDetail, backLabel = "\u2715")
                }
            }
        }
    }
}

@Composable
private fun NarrowLayout(
    state: EarlyGameState,
    onToggleChampion: (String) -> Unit,
    onIncrementComponent: (String) -> Unit,
    onDecrementComponent: (String) -> Unit,
    onFilterChanged: (String) -> Unit,
    onClearAll: () -> Unit,
    onSelectComp: (String) -> Unit,
) {
    ListColumn(
        state = state,
        onToggleChampion = onToggleChampion,
        onIncrementComponent = onIncrementComponent,
        onDecrementComponent = onDecrementComponent,
        onFilterChanged = onFilterChanged,
        onClearAll = onClearAll,
        onSelectComp = onSelectComp,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun ListColumn(
    state: EarlyGameState,
    onToggleChampion: (String) -> Unit,
    onIncrementComponent: (String) -> Unit,
    onDecrementComponent: (String) -> Unit,
    onFilterChanged: (String) -> Unit,
    onClearAll: () -> Unit,
    onSelectComp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "EARLY GAME CHAMPIONS",
            color = TftColors.textMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        OutlinedTextField(
            value = state.filterText,
            onValueChange = onFilterChanged,
            placeholder = { Text("Filter champions...", fontSize = 13.sp) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TftColors.textPrimary,
                unfocusedTextColor = TftColors.textPrimary,
                cursorColor = TftColors.chipSelectedBorder,
                focusedBorderColor = TftColors.chipSelectedBorder,
                unfocusedBorderColor = TftColors.border,
                focusedPlaceholderColor = TftColors.textMuted,
                unfocusedPlaceholderColor = TftColors.textMuted,
                focusedContainerColor = TftColors.surface,
                unfocusedContainerColor = TftColors.surface,
            ),
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
        )
        ChampionSelectorGrid(
            champions = state.earlyChampionPool,
            selectedChampions = state.selectedChampions,
            championScores = state.championScores,
            filter = state.filterText,
            onToggle = onToggleChampion,
        )

        Text(
            text = "ITEM COMPONENTS",
            color = TftColors.textMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        ComponentSelectorRow(
            componentCounts = state.componentCounts,
            componentScores = state.componentScores,
            onIncrement = onIncrementComponent,
            onDecrement = onDecrementComponent,
        )

        HorizontalDivider(color = TftColors.border)

        val nothingSelected = state.selectedChampions.isEmpty() && state.componentCounts.isEmpty()
        if (nothingSelected) {
            Text(
                text = "TIER LIST",
                color = TftColors.textMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            )
            CompRecommendationList(
                recommendations = state.allComps,
                selectedSlug = state.selectedComp?.compSlug,
                onSelect = onSelectComp,
                modifier = Modifier.weight(1f),
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "RECOMMENDED COMPS (${state.recommendations.size})",
                    color = TftColors.textMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Clear all",
                    color = TftColors.chipSelectedBorder,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable(onClick = onClearAll),
                )
            }
            CompRecommendationList(
                recommendations = state.recommendations,
                selectedSlug = state.selectedComp?.compSlug,
                onSelect = onSelectComp,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
