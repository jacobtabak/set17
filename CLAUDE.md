# Set 17

## Running the app

Always use hot reload when running the desktop app:

```
./gradlew :composeApp:hotRunDesktop
```

Do NOT use `:composeApp:run` — use `hotRunDesktop` so changes are swapped live.

## Testing

```
./gradlew :tftacademy:jvmTest
```

## Project structure

- `:tftacademy` — data layer (fetch, parse, store, query). Uses `jvm()` target.
- `:earlygame` — early game advisor logic + Compose UI. Uses `jvm("desktop")` target.
- `:composeApp` — entry point with platform-specific main classes. Uses `jvm("desktop")` target.

All three modules target: JVM, Android, iOS (x64/arm64/simulator), wasmJs.

## Static game data

Champion traits, costs, and item recipes are hardcoded (they don't change within a set):
- `tftacademy/.../champion/ChampionData.kt` — champion names, costs, traits, trait breakpoints
- `tftacademy/.../item/ItemComponentMap.kt` — item recipes from Community Dragon

Note: TFT Academy uses `TFT17_Galio` for The Mighty Mech (not `TFT17_Robot`).

## Scoring algorithm

Scoring weights are named constants at the top of `EarlyGameEngine.kt`. They are designed to be tuning levers:
- `FULL_ITEM_WEIGHT = 7` — both components for a carry item (strongest signal)
- `PARTIAL_ITEM_WEIGHT = 2` — 1 of 2 components
- `CAROUSEL_MATCH_WEIGHT = 1` — component matches carousel priority
- `CHAMPION_MATCH_WEIGHT = 1` — champion in comp's early board

All weights are multiplied by tier weight (S=3, A=2, B=1). Components are consumed in priority order (full > partial > carousel) to prevent double-counting.

Key design decision: an A-tier comp with 1 full carry item (14) outscores an S-tier comp with 2 partials (12). This is intentional — a slammable item is a stronger signal than partial component matches.

## Adaptive layout

The EarlyGameScreen uses `BoxWithConstraints` with a 600dp breakpoint:
- Wide: side-by-side (list + animated detail panel)
- Narrow: fullscreen navigation via Compose NavController

When resizing wide->narrow with a comp selected, it navigates to the detail route. When resizing narrow->wide, it pops back and restores the selection via savedStateHandle.

## Non-champion units

The comp data includes non-champion entries (Relic, IvernMinion, Summon, Flex slots) that are filtered out in the UI by checking against `ChampionData.champions`.
