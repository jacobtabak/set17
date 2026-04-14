package dev.set17.tftacademy.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class DriverFactory(private val dbPath: String? = null) {
    actual fun createDriver(): SqlDriver {
        val url = if (dbPath != null) "jdbc:sqlite:$dbPath" else JdbcSqliteDriver.IN_MEMORY
        return JdbcSqliteDriver(url).also {
            TftAcademyDatabase.Schema.create(it)
        }
    }
}
