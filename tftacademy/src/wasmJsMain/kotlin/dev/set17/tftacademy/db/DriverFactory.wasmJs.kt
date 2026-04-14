package dev.set17.tftacademy.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return createDefaultWebWorkerDriver()
    }
}
