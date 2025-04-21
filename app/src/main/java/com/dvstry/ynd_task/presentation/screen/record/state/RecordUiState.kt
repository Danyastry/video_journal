package com.dvstry.ynd_task.presentation.screen.record.state

import java.io.File

data class RecordUiState(
    val isRecording: Boolean = false,
    val recordedVideoFile: File? = null,
    val description: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)