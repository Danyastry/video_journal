package com.dvstry.ynd_task.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class DatabaseDriverFactory(private val context: Context) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = VideoDatabase.Schema,
            context = context,
            name = "video_journal.db"
        )
    }
}