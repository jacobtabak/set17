package dev.set17.tftacademy.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.SupportSQLiteDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        // Schema creation is handled async by createDatabase() in common code.
        // We just need to open/create the SQLite file.
        val callback = object : SupportSQLiteOpenHelper.Callback(1) {
            override fun onCreate(db: SupportSQLiteDatabase) {}
            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }
        val helper = androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory()
            .create(
                SupportSQLiteOpenHelper.Configuration.builder(context)
                    .name("tftacademy.db")
                    .callback(callback)
                    .build()
            )
        return AndroidSqliteDriver(helper)
    }
}
