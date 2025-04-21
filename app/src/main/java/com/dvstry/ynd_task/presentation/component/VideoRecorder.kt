package com.dvstry.ynd_task.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.io.File

@Composable
fun VideoRecorder(
    onVideoSaved: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    PermissionHandler {
        VideoRecorderContent(onVideoSaved, modifier)
    }
}