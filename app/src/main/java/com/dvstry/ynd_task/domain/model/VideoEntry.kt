package com.dvstry.ynd_task.domain.model

import java.util.Date

data class VideoEntry(
    val id: Long = 0,
    val filePath: String,
    val description: String = "",
    val thumbnailPath: String? = null,
    val createdAt: Date = Date(),
    val duration: Long = 0
)