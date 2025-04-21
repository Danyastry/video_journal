package com.dvstry.ynd_task.di

import com.dvstry.ynd_task.data.local.DatabaseDriverFactory
import com.dvstry.ynd_task.data.local.VideoDatabase
import org.koin.dsl.module

val appModule = module {
    single {
        val driverFactory = DatabaseDriverFactory(get())
        VideoDatabase(driverFactory.createDriver())
    }
}