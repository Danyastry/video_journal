package com.dvstry.ynd_task.presentation.component

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

const val TAG = "VideoRecorder"

@Composable
fun VideoRecorderContent(
    onVideoSaved: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var recording by remember { mutableStateOf<Recording?>(null) }
    var debugInfo by remember { mutableStateOf("Initializing") }

    val cameraPermissionGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    val audioPermissionGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    if (!cameraPermissionGranted || !audioPermissionGranted) {
        debugInfo = "Permissions missing!"
        return
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val preview = remember { Preview.Builder().build() }
    val previewView = remember { PreviewView(context) }
    val executor = remember { ContextCompat.getMainExecutor(context) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }

    LaunchedEffect(Unit) {
        try {
            debugInfo = "Obtaining camera provider"
            val cameraProvider = cameraProviderFuture.get()
            val cameraInfos = cameraProvider.availableCameraInfos
            Log.d(TAG, "Available cameras: ${cameraInfos.size}")

            for (cameraInfo in cameraInfos) {
                Log.d(TAG, "Camera: $cameraInfo")
            }
            debugInfo = "Setting up video recording"
            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.SD,
                        FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
                    )
                )
                .build()

            val videoCaptureObj = VideoCapture.withOutput(recorder)

            cameraProvider.unbindAll()

            debugInfo = "Binding camera"
            try {
                var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                if (cameraInfos.isNotEmpty()) {
                    try {
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val camera2 = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            videoCaptureObj
                        )
                        videoCapture = videoCaptureObj
                        debugInfo = "Camera ready"
                        Log.d(TAG, "Camera successfully initialized: $videoCapture")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error with back camera, trying front camera", e)
                        debugInfo = "Trying front camera"

                        try {
                            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                            cameraProvider.unbindAll()
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                videoCaptureObj
                            )

                            preview.setSurfaceProvider(previewView.surfaceProvider)
                            videoCapture = videoCaptureObj
                            debugInfo = "Front camera ready"
                            Log.d(TAG, "Front camera successfully initialized")
                        } catch (frontCameraException: Exception) {
                            debugInfo = "Error with both cameras"
                            Log.e(
                                TAG,
                                "Failed to initialize any camera",
                                frontCameraException
                            )
                        }
                    }
                } else {
                    debugInfo = "No cameras detected"
                }
            } catch (e: Exception) {
                debugInfo = "Binding error: ${e.message}"
                Log.e(TAG, "Camera binding error", e)
            }
        } catch (e: Exception) {
            debugInfo = "Error: ${e.message}"
            Log.e(TAG, "Camera initialization error", e)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            Log.d(TAG, "Cleaning up camera resources")
            try {
                recording?.stop()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping recording", e)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = debugInfo,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp),
            color = Color.White
        )
        FloatingActionButton(
            onClick = {
                Log.d(
                    TAG,
                    "Record button clicked: recording=${recording != null}, videoCapture=${videoCapture != null}"
                )

                val cameraPermissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                val audioPermissionGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                if (!cameraPermissionGranted || !audioPermissionGranted) {
                    debugInfo = "Missing required permissions!"
                    Log.e(
                        TAG,
                        "Permissions missing: camera=$cameraPermissionGranted, audio=$audioPermissionGranted"
                    )
                    return@FloatingActionButton
                }

                try {
                    if (recording != null) {
                        Log.d(TAG, "Stopping recording")
                        debugInfo = "Stopping recording"
                        recording?.stop()
                        recording = null
                    } else if (videoCapture != null) {
                        Log.d(TAG, "Starting recording")
                        debugInfo = "Starting recording"

                        val videoFile = createVideoFile(context)
                        val outputOptions = FileOutputOptions.Builder(videoFile).build()

                        try {
                            val newRecording = videoCapture!!.output
                                .prepareRecording(context, outputOptions)
                                .withAudioEnabled()
                                .start(executor) { event ->
                                    when (event) {
                                        is VideoRecordEvent.Start -> {
                                            debugInfo = "Recording"
                                            Log.d(TAG, "Recording started")
                                        }

                                        is VideoRecordEvent.Finalize -> {
                                            if (event.hasError()) {
                                                debugInfo = "Recording error: ${event.error}"
                                                Log.e(
                                                    TAG,
                                                    "Recording finalization error: ${event.error}"
                                                )
                                            } else {
                                                debugInfo = "Recording completed"
                                                Log.d(TAG, "Recording successfully completed")
                                                onVideoSaved(videoFile)
                                            }
                                        }

                                        else -> {}
                                    }
                                }

                            recording = newRecording
                            Log.d(TAG, "Recording successfully started")
                        } catch (securityException: SecurityException) {
                            debugInfo = "Security error: ${securityException.message}"
                            Log.e(TAG, "Security error starting recording", securityException)
                        } catch (e: Exception) {
                            debugInfo = "Recording start error: ${e.message}"
                            Log.e(TAG, "Error starting recording", e)
                        }
                    } else {
                        debugInfo = "Error: Camera not initialized"
                        Log.e(TAG, "Recording attempted, but videoCapture = null")
                    }
                } catch (e: Exception) {
                    debugInfo = "Error: ${e.message}"
                    Log.e(TAG, "General error handling recording", e)
                }
            },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(72.dp),
            containerColor = if (recording != null) Color.Red else Color.White
        ) {
            Icon(
                imageVector = if (recording != null) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (recording != null) "Stop recording" else "Start recording",
                tint = if (recording != null) Color.White else Color.Red,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}