package dev.set17.tftacademy.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return WebWorkerDriver(
            Worker(js("""new URL("@nicolo-ribaudo/choco-sqlite/worker.mjs", import.meta.url)"""))
        ).also {
            TftAcademyDatabase.Schema.create(it)
        }
    }
}
