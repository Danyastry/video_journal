package com.dvstry.ynd_task

import android.app.Application
import com.dvstry.ynd_task.di.appModule
import com.dvstry.ynd_task.di.dataModule
import com.dvstry.ynd_task.di.domainModule
import com.dvstry.ynd_task.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VideoJournalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@VideoJournalApp)
            modules(appModule, dataModule, domainModule, presentationModule)
        }
    }
}
