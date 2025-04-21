package com.dvstry.ynd_task.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.dvstry.ynd_task.data.local.VideoDatabase
import com.dvstry.ynd_task.data.mapper.toInsertParameters
import com.dvstry.ynd_task.data.mapper.toVideoEntry
import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.ynd_task.domain.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class VideoRepositoryImpl(
    private val database: VideoDatabase,
    private val context: Context
) : VideoRepository {

    override fun getVideos(): Flow<List<VideoEntry>> {
        return database.videoDatabaseQueries.getAllVideos()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toVideoEntry() } }
    }

    override suspend fun getVideoById(id: Long): VideoEntry? {
        return withContext(Dispatchers.IO) {
            database.videoDatabaseQueries.getVideoById(id)
                .executeAsOneOrNull()
                ?.toVideoEntry()
        }
    }

    override suspend fun saveVideo(video: VideoEntry): Long {
        return withContext(Dispatchers.IO) {
            val params = video.toInsertParameters()
            database.videoDatabaseQueries.insertVideo(
                file_path = params[0] as String,
                description = params[1] as String,
                thumbnail_path = params[2] as String?,
                created_at = params[3] as Long,
                duration = params[4] as Long
            )

            database.videoDatabaseQueries.getLastInsertRowId().executeAsOne()
        }
    }

    override suspend fun deleteVideo(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val video = getVideoById(id)
                video?.let {
                    val videoFile = File(it.filePath)
                    if (videoFile.exists()) {
                        videoFile.delete()
                    }
                    it.thumbnailPath?.let { thumbnailPath ->
                        val thumbnailFile = File(thumbnailPath)
                        if (thumbnailFile.exists()) {
                            thumbnailFile.delete()
                        }
                    }
                }
                database.videoDatabaseQueries.deleteVideo(id)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    override suspend fun generateThumbnail(videoPath: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, videoPath.toUri())
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull() ?: 0
                val timeUs = if (duration > 2000) 1000000 else duration * 500
                val bitmap =
                    retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                bitmap?.let {
                    val thumbnailFile = File(
                        context.cacheDir,
                        "thumbnail_${System.currentTimeMillis()}.jpg"
                    )
                    FileOutputStream(thumbnailFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    bitmap.recycle()
                    retriever.release()
                    thumbnailFile.absolutePath
                } ?: run {
                    retriever.release()
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}