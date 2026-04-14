# set17

Kotlin Multiplatform app for TFT Set 17 early game champion prioritization, powered by data from [TFT Academy](https://tftacademy.com).

## What it does

Helps you decide which champions to buy and hold during TFT stages 1-2 (before the first augment selection). Select the champions you see in your shop and the app recommends which S/A/B tier comps you're on track for, ranked by a flexibility score.

## Modules

### tftacademy

Data layer. Fetches the TFT Academy comp tier list via their SvelteKit `__data.json` endpoint, parses the indexed format, and stores everything in a local SQLDelight database.

- 44 comps with full boards, items, augments, stage-by-stage tips
- Queryable by champion, item, base component, tier, difficulty, style
- Static data: item recipes (Community Dragon), champion traits (mobalytics)

### earlygame

Early game advisor logic + Compose UI.

- **EarlyGameEngine**: Scores champions by flexibility (how many S/A/B comps use them early) and recommends comps based on your selected champions
- **Adaptive layout**: Side-by-side panels on wide screens, fullscreen navigation on narrow/mobile
- **Comp detail**: Final board with item icons (loaded from TFT Academy CDN), active traits, early comp, carousel priority, stage tips

### composeApp

Entry point. Compose Navigation with type-safe routes. Platform targets:

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

## Data flow

```
TFT Academy __data.json  ->  SvelteKitDataParser  ->  SQLDelight DB
                                                          |
                                    EarlyGameEngine  <----+
                                         |
                                    EarlyGameScreen (Compose UI)
```

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
