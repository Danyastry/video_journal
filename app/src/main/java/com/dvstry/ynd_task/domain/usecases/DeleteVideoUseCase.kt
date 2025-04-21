package com.dvstry.ynd_task.domain.usecases

import com.dvstry.ynd_task.domain.repository.VideoRepository

class DeleteVideoUseCase(private val repository: VideoRepository) {
    suspend operator fun invoke(id: Long): Result<Boolean> {
        return try {
            val result = repository.deleteVideo(id)
            if (result) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete video"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}