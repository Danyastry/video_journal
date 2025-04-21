package com.dvstry.ynd_task.domain.usecases

import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.ynd_task.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow

class GetVideosUseCase(private val repository: VideoRepository) {
    operator fun invoke(): Flow<List<VideoEntry>> {
        return repository.getVideos()
    }
}