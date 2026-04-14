package dev.set17.earlygame.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.set17.earlygame.EarlyGameEngine
import dev.set17.earlygame.model.EarlyGameState
import dev.set17.earlygame.model.GamePhase
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
            withContext(Dispatchers.Default) {
                if (!repo.hasData()) {
                    repo.refresh()
                }
                engine.init()
            }
            state = state.copy(
                loading = false,
                earlyChampionPool = engine.getEarlyChampionPool(),
                championScores = engine.scoreChampions(),
                lateChampionScores = engine.scoreLateChampions(),
                componentScores = engine.scoreComponents(),
                itemScores = engine.scoreItems(),
                allComps = engine.allComps(),
                unknownEmblems = engine.unknownEmblems,
            )
        } catch (e: Exception) {
            state = state.copy(loading = false, error = e.message)
        }
    }

    LaunchedEffect(state.selectedChampions, state.componentCounts, state.fullItemCounts, state.selectedTab) {
        val recs = engine.recommend(
            state.selectedChampions,
            state.componentCounts,
            state.fullItemCounts,
            state.selectedTab,
        )
        state = state.copy(recommendations = recs)
    }

    Box(modifier = Modifier.fillMaxSize().background(TftColors.background).statusBarsPadding()) {
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
                val scope = rememberCoroutineScope()
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
                            fullItemCounts = emptyMap(),
                            filterText = "",
                            selectedComp = null,
                        )
                    }
                    val incrementFullItem = { item: String ->
                        val cur = state.fullItemCounts[item] ?: 0
                        if (cur < 3) state = state.copy(fullItemCounts = state.fullItemCounts + (item to cur + 1))
                    }
                    val decrementFullItem = { item: String ->
                        val cur = state.fullItemCounts[item] ?: 0
                        if (cur > 0) {
                            val new = if (cur == 1) state.fullItemCounts - item else state.fullItemCounts + (item to cur - 1)
                            state = state.copy(fullItemCounts = new)
                        }
                    }
                    val onTabChanged = { phase: GamePhase ->
                        state = state.copy(selectedTab = phase)
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
                            onIncrementFullItem = incrementFullItem,
                            onDecrementFullItem = decrementFullItem,

                            onTabChanged = onTabChanged,
                            onClearAll = clearAll,
                            onSelectComp = { slug ->
                                if (state.selectedComp?.compSlug == slug) {
                                    state = state.copy(selectedComp = null)
                                } else {
                                    scope.launch {
                                        val comp = engine.getFullComp(slug)
                                        state = state.copy(selectedComp = comp)
                                    }
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
                            onIncrementFullItem = incrementFullItem,
                            onDecrementFullItem = decrementFullItem,

                            onTabChanged = onTabChanged,
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
    onIncrementFullItem: (String) -> Unit,
    onDecrementFullItem: (String) -> Unit,

    onTabChanged: (GamePhase) -> Unit,
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
            onIncrementFullItem = onIncrementFullItem,
            onDecrementFullItem = onDecrementFullItem,

            onTabChanged = onTabChanged,
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
                    CompDetailPanel(comp, onBack = onCloseDetail, useCloseIcon = true)
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
    onIncrementFullItem: (String) -> Unit,
    onDecrementFullItem: (String) -> Unit,

    onTabChanged: (GamePhase) -> Unit,
    onClearAll: () -> Unit,
    onSelectComp: (String) -> Unit,
) {
    ListColumn(
        state = state,
        onToggleChampion = onToggleChampion,
        onIncrementComponent = onIncrementComponent,
        onDecrementComponent = onDecrementComponent,
        onFilterChanged = onFilterChanged,
        onIncrementFullItem = onIncrementFullItem,
        onDecrementFullItem = onDecrementFullItem,
        onTabChanged = onTabChanged,
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
    onIncrementFullItem: (String) -> Unit,
    onDecrementFullItem: (String) -> Unit,

    onTabChanged: (GamePhase) -> Unit,
    onClearAll: () -> Unit,
    onSelectComp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLate = state.selectedTab == GamePhase.LATE
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    val nothingSelected = state.selectedChampions.isEmpty() &&
        state.componentCounts.isEmpty() &&
        state.fullItemCounts.isEmpty()
    val compList = if (nothingSelected) state.allComps else state.recommendations

    LaunchedEffect(compList) {
        listState.scrollToItem(0)
    }

    Column(modifier = modifier) {
        // Tabs — fixed, not scrollable
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TabLabel("EARLY GAME", selected = !isLate, onClick = { onTabChanged(GamePhase.EARLY) })
            TabLabel("LATE GAME", selected = isLate, onClick = { onTabChanged(GamePhase.LATE) })
        }

    androidx.compose.foundation.lazy.LazyColumn(
        state = listState,
        modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (state.unknownEmblems.isNotEmpty()) {
            item {
                Text(
                    text = "Unknown emblems in data: ${state.unknownEmblems.joinToString { it.substringAfterLast("_") }}. Item scoring may be incomplete.",
                    color = TftColors.missingChampion,
                    fontSize = 11.sp,
                )
            }
        }

        // Champions
        item {
            var champsExpanded by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CollapsibleHeader(
                    text = "CHAMPIONS",
                    expanded = champsExpanded,
                    selectedCount = state.selectedChampions.size,
                    onClick = { champsExpanded = !champsExpanded },
                )
                if (champsExpanded) {
                    OutlinedTextField(
                        value = state.filterText,
                        onValueChange = onFilterChanged,
                        placeholder = { Text(if (isLate) "Search champions..." else "Filter champions...", fontSize = 13.sp) },
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
                    if (isLate) {
                        if (state.filterText.isNotBlank()) {
                            ChampionSelectorGrid(
                                champions = emptyList(),
                                selectedChampions = state.selectedChampions,
                                championScores = state.lateChampionScores,
                                filter = state.filterText,
                                onToggle = onToggleChampion,
                            )
                        }
                        if (state.selectedChampions.isNotEmpty()) {
                            ChampionSelectorGrid(
                                champions = state.selectedChampions.toList(),
                                selectedChampions = state.selectedChampions,
                                championScores = state.lateChampionScores,
                                filter = "",
                                onToggle = onToggleChampion,
                            )
                        }
                    } else {
                        ChampionSelectorGrid(
                            champions = state.earlyChampionPool,
                            selectedChampions = state.selectedChampions,
                            championScores = state.championScores,
                            filter = state.filterText,
                            onToggle = onToggleChampion,
                        )
                    }
                }
            }
        }

        // Item components
        item {
            val componentTotal = state.componentCounts.values.sum()
            var componentsExpanded by remember { mutableStateOf(true) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CollapsibleHeader(
                    text = "ITEM COMPONENTS",
                    expanded = componentsExpanded,
                    selectedCount = componentTotal,
                    onClick = { componentsExpanded = !componentsExpanded },
                )
                if (componentsExpanded) {
                    ComponentSelectorRow(
                        componentCounts = state.componentCounts,
                        componentScores = state.componentScores,
                        onIncrement = onIncrementComponent,
                        onDecrement = onDecrementComponent,
                    )
                }
            }
        }

        // Full items + emblems (late game only)
        if (isLate) {
            item {
                val fullItemTotal = state.fullItemCounts.entries
                    .count { it.key !in dev.set17.tftacademy.item.ItemComponentMap.emblemApiNameToTrait }
                var itemsExpanded by remember { mutableStateOf(false) }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CollapsibleHeader(
                        text = "COMPLETED ITEMS",
                        expanded = itemsExpanded,
                        selectedCount = fullItemTotal,
                        onClick = { itemsExpanded = !itemsExpanded },
                    )
                    if (itemsExpanded) {
                        FullItemSelector(
                            itemCounts = state.fullItemCounts,
                            itemScores = state.itemScores,
                            onIncrement = onIncrementFullItem,
                            onDecrement = onDecrementFullItem,
                        )
                    }
                }
            }
            item {
                val emblemTotal = state.fullItemCounts.entries
                    .count { it.key in dev.set17.tftacademy.item.ItemComponentMap.emblemApiNameToTrait }
                var emblemsExpanded by remember { mutableStateOf(false) }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CollapsibleHeader(
                        text = "EMBLEMS",
                        expanded = emblemsExpanded,
                        selectedCount = emblemTotal,
                        onClick = { emblemsExpanded = !emblemsExpanded },
                    )
                    if (emblemsExpanded) {
                        EmblemSelector(
                            itemCounts = state.fullItemCounts,
                            itemScores = state.itemScores,
                            onIncrement = onIncrementFullItem,
                            onDecrement = onDecrementFullItem,
                        )
                    }
                }
            }
        }

        item { HorizontalDivider(color = TftColors.border) }

        // Recommendations header
        item {
            if (nothingSelected) {
                Text(
                    text = "TIER LIST",
                    color = TftColors.textMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
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
            }
        }

        // Comp list items inline
        items(compList, key = { it.comp.compSlug }) { rec ->
            CompRecommendationCard(
                recommendation = rec,
                isSelected = rec.comp.compSlug == state.selectedComp?.compSlug,
                onClick = { onSelectComp(rec.comp.compSlug) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Bottom padding for navigation bars / IME — only visible when scrolled to bottom
        item {
            Spacer(
                Modifier
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .then(Modifier.imePadding())
            )
        }
    }
    } // end outer Column
}

@Composable
private fun CollapsibleHeader(
    text: String,
    expanded: Boolean,
    selectedCount: Int = 0,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = if (expanded)
                Icons.Filled.KeyboardArrowDown
            else
                Icons.Filled.KeyboardArrowRight,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = TftColors.textMuted,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            color = TftColors.textMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        if (!expanded && selectedCount > 0) {
            Text(
                text = "($selectedCount selected)",
                color = TftColors.chipSelectedBorder,
                fontSize = 11.sp,
            )
        }
    }
}

@Composable
private fun TabLabel(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .clickable(onClick = onClick)
            .padding(bottom = 4.dp),
    ) {
        Text(
            text = text,
            color = if (selected) TftColors.textPrimary else TftColors.textMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (selected) TftColors.chipSelectedBorder else TftColors.background),
        )
    }
}
