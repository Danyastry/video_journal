package com.dvstry.ynd_task.di

import com.dvstry.ynd_task.domain.usecases.DeleteVideoUseCase
import com.dvstry.ynd_task.domain.usecases.GetVideosUseCase
import com.dvstry.ynd_task.domain.usecases.SaveVideoUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetVideosUseCase(repository = get()) }
    factory { SaveVideoUseCase(repository = get()) }
    factory { DeleteVideoUseCase(repository = get()) }
}