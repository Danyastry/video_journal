package com.dvstry.ynd_task.presentation.screen.record.component

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.dvstry.ynd_task.presentation.component.VideoPlayer
import kotlinx.coroutines.delay

@Composable
fun RecordedVideoPreview(
    videoUri: Uri,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    isSaving: Boolean
) {
    var videoVisible by remember { mutableStateOf(false) }
    var formVisible by remember { mutableStateOf(false) }
    var actionsVisible by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        delay(200)
        videoVisible = true
        delay(300)
        formVisible = true
        delay(200)
        actionsVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = videoVisible,
            enter = fadeIn(tween(600)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                VideoPlayer(
                    videoUri = videoUri,
                    autoPlay = true,
                    showControls = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(
            visible = formVisible,
            enter = fadeIn(tween(500)) + slideInVertically(
                initialOffsetY = { 50 },
                animationSpec = tween(500)
            )
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChanged,
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedVisibility(
            visible = actionsVisible,
            enter = fadeIn(tween(500)) + slideInVertically(
                initialOffsetY = { 50 },
                animationSpec = tween(500)
            )
        ) {
            if (isSaving) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Saving your video...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ActionButton(
                        text = "Discard",
                        icon = Icons.Rounded.Delete,
                        color = MaterialTheme.colorScheme.error,
                        onClick = onDiscard,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    ActionButton(
                        text = "Save",
                        icon = Icons.Rounded.Check,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = onSave,
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
