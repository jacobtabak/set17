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

Champion traits and item recipes are hardcoded (they don't change within a set):
- `tftacademy/src/commonMain/kotlin/.../champion/ChampionData.kt` — champion names, costs, traits
- `tftacademy/src/commonMain/kotlin/.../item/ItemComponentMap.kt` — item recipes from Community Dragon

## Adaptive layout

The EarlyGameScreen uses `BoxWithConstraints` with a 600dp breakpoint:
- Wide: side-by-side (list + detail panel)
- Narrow: fullscreen navigation via Compose NavController

When resizing wide->narrow with a comp selected, it navigates to the detail route. When resizing narrow->wide, it pops back and restores the selection via savedStateHandle.
