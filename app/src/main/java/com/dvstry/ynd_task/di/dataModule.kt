package com.dvstry.ynd_task.di

import com.dvstry.ynd_task.data.repository.VideoRepositoryImpl
import com.dvstry.ynd_task.domain.repository.VideoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<VideoRepository> {
        VideoRepositoryImpl(
            database = get(),
            context = androidContext()
        )
    }
}