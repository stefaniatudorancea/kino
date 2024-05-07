package com.example.kino.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.kino.data.ExerciseDataDb

@Composable
fun VideoPlayer(uri: String) {
    val context = LocalContext.current
    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build().also { player ->
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }
    }
    DisposableEffect(exoPlayer, uri) {
        onDispose {
            exoPlayer.release()
        }
    }
    AndroidView(
        modifier = Modifier.size(300.dp, 520.dp),
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        update = { playerView ->
        }
    )
}

@Composable
fun ExerciseItem(exercise: ExerciseDataDb) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Name: ${exercise.name}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Description: ${exercise.description}")
            exercise.videoUri?.let { uri ->
                Text(text = "Video URL: $uri")
            }
        }
    }
}

@Composable
fun CreateExFieldComponent(
    labelValue: String,
    onTextSelected: (String) -> Unit,
    fieldForNumbers: Boolean = false
){
    val keyboardController = LocalSoftwareKeyboardController.current
    val textValue = remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 5.dp)
            .clip(componentShapes.small),
        shape = RoundedCornerShape(50),
        value = textValue.value,
        label = { Text(text = labelValue) },
        onValueChange = {newValue ->
            if(!fieldForNumbers){
                textValue.value = newValue
            }else{
                textValue.value = newValue.filter { it.isDigit() }
            }
            onTextSelected(newValue)
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        singleLine = false,
    )
}


