# set17

Kotlin Multiplatform app for TFT Set 17 comp recommendations, powered by data from [TFT Academy](https://tftacademy.com).

**[Try it in your browser](https://jacobtabak.github.io/set17/)**

## What it does

Two modes for different stages of the game:

**Early Game** — You're in stages 1-2 before the first augment. Select the champions you see in shop and the item components you have. The app recommends S/A/B tier comps ranked by how well your current board and items fit the comp's early game plan.

**Late Game** — You're looking for your final comp. Search for specific champions, select completed items and emblems you already have, and the app shows which comps your board fits best.

### Input signals

- **Champions**: Matched against earlyComp (early tab) or finalComp (late tab)
- **Item components**: Base components (10 types including Spatula/Frying Pan) with quantity support
- **Completed items**: Full items you already have — direct match against comp item slots (late game)
- **Emblems**: Craftable trait emblems (16 types) — matched as build-defining items

### Scoring

Comps ranked by a combined score. All weights multiplied by tier (S=3, A=2, B=1). Items are scored by role in the comp (carry > emblem > tank > support) with components consumed in priority order to prevent double-counting.

When nothing is selected, a full tier list is shown.

## Modules

### tftacademy

Data layer. Fetches the TFT Academy comp tier list via their SvelteKit `__data.json` endpoint, parses the indexed format, and stores everything in a local SQLDelight database.

- 44 comps with full boards, items, augments, stage-by-stage tips
- Queryable by champion, item, base component, tier, difficulty, style
- Static data: item recipes + display names (Community Dragon), champion traits/costs (mobalytics), trait breakpoints, emblem recipes, defensive item classification

### earlygame

Game advisor logic + Compose UI.

- **EarlyGameEngine**: Phase-aware scoring (early vs late). 9-phase item matching with configurable weights in `ScoringConfig`. Tank detection via defensive item set. Emblem detection with unknown emblem warnings.
- **Early/Late tabs**: Fixed tab bar with shared champion/item state. Early tab shows champion flex grid, late tab shows search-to-add champions with completed items and emblems.
- **Adaptive layout**: Side-by-side panels on wide screens (animated open/close), fullscreen navigation on narrow/mobile. Preserves selection across resize transitions.
- **Collapsible sections**: Champions, item components, completed items, emblems — each with selected count indicator when collapsed.
- **Comp detail**: Aligned columns with cost-colored champion names, item icons (TFT Academy CDN), activated traits at breakpoints, early comp, carousel priority, stage tips.
- **Item display**: Icon-based selectors with +/- quantity controls. Item names use current in-game display names from Community Dragon.
- **Scrollable**: Entire screen is a single LazyColumn with tabs pinned at top.

### composeApp

Entry point. Compose Navigation with type-safe `@Serializable` routes. Platform targets:

- Desktop (JVM)
- Android
- iOS (x64, arm64, simulator)
- wasmJs (browser)

## Platforms

| Platform | Entry point | Status |
|----------|------------|--------|
| Desktop | `composeApp/src/desktopMain` | Working |
| Android | `composeApp/src/androidMain` (MainActivity) | Compiles |
| iOS | `composeApp/src/iosMain` (MainViewController) | Compiles (needs Xcode project) |
| Web | `composeApp/src/wasmJsMain` | Working |

## Tech stack

- **Kotlin Multiplatform** 2.2.10
- **Compose Multiplatform** 1.10.3
- **Compose Navigation** 2.9.2 (type-safe routes)
- **Ktor** 3.1.3 (HTTP)
- **kotlinx.serialization** (JSON)
- **SQLDelight** 2.3.2 (local database, async for wasmJs)
- **Coil** 3.4.0 (async image loading for item icons)
- **Compose Hot Reload** 1.0.0
- **Gradle** 8.14

## Building and running

```bash
# Run the desktop app (with hot reload)
./gradlew :composeApp:hotRunDesktop

# Run the web app (wasmJs)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Run tests
./gradlew :tftacademy:jvmTest

# Compile Android
./gradlew :composeApp:compileDebugKotlinAndroid

# Compile desktop
./gradlew :composeApp:compileKotlinDesktop
```

## Scoring algorithm

All weights multiplied by tier weight (S=3, A=2, B=1). Components/items consumed in priority order.

| Phase | Signal | Weight | Description |
|-------|--------|--------|-------------|
| 0 | Full item direct match | role weight × tier | User already has the completed item |
| 1 | Carry item 1-2 full | 7 × tier | Both components available |
| 2 | Carry item 3 full | 5 × tier | Third carry item, slightly less priority |
| 3 | Emblem full | 7 × tier | Build-defining emblem craftable |
| 4 | Tank item full | 6 × tier | Tank item craftable |
| 5 | Support item full | 4 × tier | Non-carry/tank item craftable |
| 6-8 | Partial matches | 1-2 × tier | 1 of 2 components available |
| 9 | Carousel match | 1 × tier | Component in carousel priority |
| — | Champion match | 1 × tier | Champion in early/final board |

Weights are tunable constants in `ScoringConfig.kt`.

## Database schema

```
comp (44 rows)
 |-- comp_champion (role: final/early/alt/maxcap)
 |    |-- champion_item (equipped items)
 |    +-- maxcap_predecessor (level 9 replacements)
 |-- comp_augment
 |-- comp_augment_type
 |-- comp_carousel
 +-- comp_tip

item_recipe (base component -> completed item mappings)
```
