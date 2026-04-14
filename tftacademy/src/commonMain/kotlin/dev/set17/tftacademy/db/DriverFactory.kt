package dev.set17.tftacademy.db

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

/** Create the database, initializing the schema if needed. */
suspend fun createDatabase(driverFactory: DriverFactory): TftAcademyDatabase {
    val driver = driverFactory.createDriver()
    TftAcademyDatabase.Schema.create(driver).await()
    return TftAcademyDatabase(driver)
}
