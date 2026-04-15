package dev.set17.tftacademy.db

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

/** Create the database, initializing the schema if needed. */
suspend fun createDatabase(driverFactory: DriverFactory): TftAcademyDatabase {
    val driver = driverFactory.createDriver()
    driver.execute(null, "PRAGMA foreign_keys = ON", 0)
    try {
        TftAcademyDatabase.Schema.create(driver).await()
    } catch (e: Exception) {
        if (e.message?.contains("already exists", ignoreCase = true) != true) throw e
    }
    return TftAcademyDatabase(driver)
}
