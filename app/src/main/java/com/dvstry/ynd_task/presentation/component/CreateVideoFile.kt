package com.dvstry.ynd_task.presentation.component

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun createVideoFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
    val fileName = "VIDEO_$timeStamp.mp4"
    val storageDir = context.getExternalFilesDir(null)
    return File(storageDir, fileName)
}