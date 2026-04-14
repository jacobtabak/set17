package dev.set17

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.set17.tftacademy.db.DriverFactory

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Set 17 — Early Game Advisor",
        state = rememberWindowState(width = 1200.dp, height = 800.dp),
    ) {
        App(DriverFactory("tftacademy.db"))
    }
}
