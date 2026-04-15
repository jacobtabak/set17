# Set 17

## Running the app

Desktop (hot reload):
```
./gradlew :composeApp:hotRunDesktop
```

Web (wasmJs):
```
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```
Requires 4GB heap (`-Xmx4g` in gradle.properties). First build is slow (~5min).

## Testing

```
./gradlew :tftacademy:jvmTest
```

## Project structure

- `:tftacademy` — data layer (fetch, parse, store, query). Uses `jvm()` target.
- `:earlygame` — game advisor logic + Compose UI. Uses `jvm("desktop")` target.
- `:composeApp` — entry point with platform-specific main classes. Uses `jvm("desktop")` target.

All three modules target: JVM, Android, iOS (x64/arm64/simulator), wasmJs.

## SQLDelight async

SQLDelight is configured with `generateAsync = true` because the wasmJs Web Worker driver is async. This means:
- All DB operations are suspend functions
- Use `awaitAsList()`, `awaitAsOne()`, `awaitAsOneOrNull()` from `async-extensions` (NOT `executeAsList()` etc.)
- Schema creation uses `Schema.create(driver).await()`
- The `createDatabase()` function in `DriverFactory.kt` handles schema creation for all platforms
- DriverFactory implementations just return a raw driver — no schema creation

Desktop persists to `tftacademy.db` file. wasmJs uses IndexedDB via Web Worker.

## wasmJs specifics

- Requires `@cashapp/sqldelight-sqljs-worker` and `sql.js` npm dependencies (in tftacademy build)
- Requires `copy-webpack-plugin` npm dependency (in composeApp build) 
- `webpack.config.d/sqljs.js` configures fallbacks for `fs`/`path` and copies `sql-wasm.wasm`
- `Dispatchers.IO` doesn't exist in wasmJs — use `Dispatchers.Default`
- Unicode characters don't render in Skiko/wasmJs — use Material Icons instead
- `ComposeViewport(body)` instead of `CanvasBasedWindow` (deprecated in Compose 1.10+)
- `index.html` in `wasmJsMain/resources/` loads `composeApp.js`

## Static game data

Champion traits, costs, and item recipes are hardcoded (they don't change within a set):
- `tftacademy/.../champion/ChampionData.kt` — champion names, costs, traits, trait breakpoints
- `tftacademy/.../item/ItemComponentMap.kt` — item recipes, emblem recipes (by trait name), emblem API name mapping, display names (Community Dragon), defensive item set

Note: TFT Academy uses `TFT17_Galio` for The Mighty Mech (not `TFT17_Robot`).

## Display names

Item display names come from `ItemComponentMap.displayName()` — many items were renamed (e.g. Statikk Shiv → Void Staff, Guardian Angel → Edge of Night). Champion display names come from `ChampionData.displayName()`.

## Emblem forward compatibility

Emblem recipes are keyed by trait name in `emblemRecipes`. The `emblemApiNameToTrait` map connects TFT Academy's API names to trait names. When new emblem API names appear in data, add one line to `emblemApiNameToTrait`. Unknown emblems trigger a UI warning and console log.

## Scoring algorithm

Scoring weights are named constants in `ScoringConfig.kt`. Key design decisions:
- `CARRY_ITEM_WEIGHT = 7`, `CARRY_ITEM_3_WEIGHT = 5` — first two carry items equal, third slightly less
- `EMBLEM_ITEM_WEIGHT = 7` — emblems are build-defining, same as top carry items
- `TANK_ITEM_WEIGHT = 6` — between carry items 1-2 and item 3
- An A-tier comp with 1 full carry item (14) outscores an S-tier comp with 2 partials (12)
- Components consumed in priority order: full items → carry → emblem → tank → support → partials → carousel

## UI architecture

- **Tabs**: Early Game / Late Game — fixed at top, not scrollable
- **ListColumn**: Single `LazyColumn` with all sections (champions, components, items, emblems, recommendations)
- **Collapsible sections**: All sections collapsible with "(N selected)" indicator when collapsed. Uses Material Icons for arrows.
- **Adaptive layout**: `BoxWithConstraints` with 600dp breakpoint. Wide = side-by-side, narrow = fullscreen nav. Selection preserved via `savedStateHandle` during resize.
- **Icons**: Use Material Icons (not Unicode characters) for cross-platform compatibility.
- **Bottom insets**: Navigation bar and IME padding as last item in LazyColumn (only visible at scroll end)

## Non-champion units

The comp data includes non-champion entries (Relic, Summon, Flex slots) that are filtered out in the UI by checking against `ChampionData.champions`. Note: TFT Academy uses `TFT17_IvernMinion` for Meepsie — `ChampionData` has entries for both apiNames.
