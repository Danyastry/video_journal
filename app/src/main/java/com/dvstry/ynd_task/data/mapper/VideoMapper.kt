package com.dvstry.ynd_task.data.mapper

import com.dvstry.ynd_task.domain.model.VideoEntry
import com.dvstry.yndtask.data.local.VideoEntity
import java.util.Date

fun VideoEntity.toVideoEntry(): VideoEntry {
    return VideoEntry(
        id = id,
        filePath = file_path,
        description = description,
        thumbnailPath = thumbnail_path,
        createdAt = Date(created_at),
        duration = duration
    )
}

fun VideoEntry.toInsertParameters(): List<Any?> {
    return listOf(
        filePath,
        description,
        thumbnailPath,
        createdAt.time,
        duration
    )
}