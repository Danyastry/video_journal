package com.dvstry.ynd_task.domain.repository

import com.dvstry.ynd_task.domain.model.VideoEntry
import kotlinx.coroutines.flow.Flow

interface VideoRepository {
    fun getVideos(): Flow<List<VideoEntry>>
    suspend fun getVideoById(id: Long): VideoEntry?
    suspend fun saveVideo(video: VideoEntry): Long
    suspend fun deleteVideo(id: Long): Boolean
    suspend fun generateThumbnail(videoPath: String): String?
}