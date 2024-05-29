package com.example.kino.components

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.kino.R
import com.example.kino.data.ExerciseDataDb
import com.example.kino.rules.exercise.ExerciseViewModel
import com.example.kino.rules.routine.RoutineViewModel

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
fun SeeVideoPlayer(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true  // Autoplay when ready
        }
    }
    LaunchedEffect(videoUrl) {
        if (videoUrl.isNotBlank()) {
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }
    AndroidView(
        modifier = Modifier.size(300.dp, 520.dp),
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    )
}


@Composable
fun ExerciseItem(exercise: ExerciseDataDb, onCardClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 25.dp)
            .clickable(onClick = onCardClicked),
        colors = CardDefaults.cardColors(colorResource(id = R.color.cardBlue))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${exercise.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${exercise.description}",
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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

@Composable
fun ExerciseDetailsCard(label: String, value: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 50.dp)
            .border(2.dp, Color.Gray, RoundedCornerShape(30.dp)),
        shape = RoundedCornerShape(30),
        colors = CardDefaults.cardColors(colorResource(id = R.color.white))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "$label", style = MaterialTheme.typography.bodyMedium)
            Text(text = "$value", style = MaterialTheme.typography.bodyLarge)

        }
    }
}

@Composable
fun DeleteExerciseDialog(exerciseViewModel: ExerciseViewModel = viewModel()) {
    val showDialog = exerciseViewModel.showDeleteExerciseDialog.collectAsState()
    val routine = exerciseViewModel.selectedExercise.value
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { exerciseViewModel.dismissDeleteExerciseDialog() },
            text = { Text(stringResource(id = R.string.delete_exercise)) },
            confirmButton = {
                Button(
                    onClick = {
                        if (routine != null) {
                            exerciseViewModel.deleteExercise(routine)
                            exerciseViewModel.dismissDeleteExerciseDialog()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue))
                ) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = { exerciseViewModel.dismissDeleteExerciseDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue))
                ) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }
}


