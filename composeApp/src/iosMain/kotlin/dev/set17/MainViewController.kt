package dev.set17

import androidx.compose.ui.window.ComposeUIViewController
import dev.set17.tftacademy.db.DriverFactory

fun MainViewController() = ComposeUIViewController { App(DriverFactory()) }
