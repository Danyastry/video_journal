package com.dvstry.ynd_task.presentation.screen.record

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.core.net.toUri
import com.dvstry.ynd_task.presentation.component.PermissionHandler
import com.dvstry.ynd_task.presentation.component.VideoRecorder
import com.dvstry.ynd_task.presentation.screen.record.component.RecordedVideoPreview
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordScreen(
    viewModel: RecordViewModel = koinViewModel(),
    onNavigateToFeed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var animateContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        animateContent = true
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Video saved successfully!")
            onNavigateToFeed()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            PermissionHandler {
                AnimatedVisibility(
                    visible = animateContent,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(300))
                ) {
                    if (uiState.recordedVideoFile != null) {
                        RecordedVideoPreview(
                            videoUri = uiState.recordedVideoFile!!.toUri(),
                            description = uiState.description,
                            onDescriptionChanged = viewModel::updateDescription,
                            onSave = viewModel::saveVideoEntry,
                            onDiscard = viewModel::discardVideo,
                            isSaving = uiState.isSaving
                        )
                    } else {
                        VideoRecorder(
                            onVideoSaved = { file ->
                                viewModel.onVideoSaved(file)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
