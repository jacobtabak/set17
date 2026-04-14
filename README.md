# set17

Kotlin Multiplatform app for TFT Set 17 early game decision-making, powered by data from [TFT Academy](https://tftacademy.com).

## What it does

Helps you decide which champions to buy and which items to build during TFT stages 1-2 (before the first augment selection). Select the champions and item components you have, and the app recommends S/A/B tier comps ranked by how well your current board and items fit.

**Two input signals:**
- **Champions**: Which early game units you have — matched against each comp's early board
- **Item components**: Which base components you have (BF Sword, Rod, etc.) — matched against the carry's items in each comp

**Scoring**: Comps are ranked by a combined score. Having a fully craftable carry item is the strongest signal, weighted by comp tier (S > A > B). Components are consumed as they match to prevent double-counting. When nothing is selected, a full tier list is shown.

## Modules

### tftacademy

Data layer. Fetches the TFT Academy comp tier list via their SvelteKit `__data.json` endpoint, parses the indexed format, and stores everything in a local SQLDelight database.

- 44 comps with full boards, items, augments, stage-by-stage tips
- Queryable by champion, item, base component, tier, difficulty, style
- Static data: item recipes (Community Dragon), champion traits and costs (mobalytics), trait breakpoints

### earlygame

Early game advisor logic + Compose UI.

- **EarlyGameEngine**: Scores champions and item components by flexibility across S/A/B comps. Recommends comps based on champion matches and item craftability with tier-weighted scoring. Scoring weights are documented constants that can be tuned.
- **Adaptive layout**: Side-by-side panels on wide screens (animated open/close), fullscreen navigation on narrow/mobile. Preserves selection across resize transitions.
- **Comp detail**: Final board with cost-colored champion names, item icons (loaded from TFT Academy CDN), activated traits (only those hitting breakpoints), early comp, carousel priority, stage tips
- **Champion selector**: Filterable grid with cost-colored borders and flex ratings. Search finds all champions, not just early-game ones. Zero-flex champions hidden unless searched or selected.
- **Item component selector**: +/- stepper for each of the 8 base components, supporting multiple counts (e.g. 2x BF Sword for Deathblade)

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
| Web | `composeApp/src/wasmJsMain` | Compiles |

## Tech stack

- **Kotlin Multiplatform** 2.2.10
- **Compose Multiplatform** 1.8.2
- **Compose Navigation** 2.9.2 (type-safe routes)
- **Ktor** 3.1.3 (HTTP)
- **kotlinx.serialization** (JSON)
- **SQLDelight** 2.0.2 (local database)
- **Coil** 3.4.0 (async image loading for item icons)
- **Compose Hot Reload** 1.0.0
- **Gradle** 8.14

## Building and running

```bash
# Run the desktop app (with hot reload)
./gradlew :composeApp:hotRunDesktop

# Run tests
./gradlew :tftacademy:jvmTest

# Compile Android
./gradlew :composeApp:compileDebugKotlinAndroid

# Compile desktop
./gradlew :composeApp:compileKotlinDesktop
```

## Scoring algorithm

Comps are ranked by a single combined score. All weights are multiplied by tier weight (S=3, A=2, B=1).

| Signal | Weight | Description |
|--------|--------|-------------|
| Full carry item | 7 × tier | Both components available — can slam immediately |
| Partial carry item | 2 × tier | 1 of 2 components available |
| Carousel match | 1 × tier | Component matches comp's carousel priority |
| Champion match | 1 × tier | Champion in comp's early board |

Components are consumed in priority order (full > partial > carousel) to prevent double-counting. Comps require a non-zero score to appear.

These weights are tunable constants in `EarlyGameEngine.kt`.

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
