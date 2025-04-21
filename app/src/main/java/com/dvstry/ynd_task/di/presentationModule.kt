package com.dvstry.ynd_task.di

import com.dvstry.ynd_task.presentation.screen.feed.FeedViewModel
import com.dvstry.ynd_task.presentation.screen.record.RecordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        FeedViewModel(
            getVideosUseCase = get(),
            deleteVideoUseCase = get()
        )
    }
    viewModel {
        RecordViewModel(
            saveVideoUseCase = get()
        )
    }
}