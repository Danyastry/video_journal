package com.dvstry.ynd_task.domain.usecases

import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.ynd_task.domain.repository.VideoRepository

class SaveVideoUseCase(private val repository: VideoRepository) {
    suspend operator fun invoke(filePath: String, description: String = ""): Result<Long> {
        return try {
            val thumbnailPath = repository.generateThumbnail(filePath)
            val videoEntry = VideoEntry(
                filePath = filePath,
                description = description,
                thumbnailPath = thumbnailPath
            )
            val id = repository.saveVideo(videoEntry)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}