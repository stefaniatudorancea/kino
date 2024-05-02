package com.example.kino.screens.doctorScreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.CreateExFieldComponent
import com.example.kino.components.VideoPlayer
import com.example.kino.rules.createExercise.CreateExerciseUIEvent
import com.example.kino.rules.createExercise.CreateExerciseViewModel
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun CreateExerciseScreen(navigationViewModel: NavigationViewModel = viewModel(),createExerciseViewModel: CreateExerciseViewModel = viewModel())
{
    val uiState = createExerciseViewModel.createExerciseUIState.value
    val pickVideoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { createExerciseViewModel.onEvent(CreateExerciseUIEvent.VideoSelected(it)) }
    }
    Scaffold(
        topBar = {
            AppToolbar(toolbarTitle = stringResource(id = R.string.create_exercise), isDoctor = true)
        },
    ){paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(horizontal = 0.dp, vertical = 10.dp)
                        .padding(top = 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    BackButton()
                }
                CreateExFieldComponent(
                    labelValue = stringResource(id = R.string.exercise_description),
                    onTextSelected = {createExerciseViewModel.onEvent(CreateExerciseUIEvent.DescriptionChanged(it))},
                    fieldForNumbers = false
                )
                CreateExFieldComponent(
                    labelValue = stringResource(id = R.string.nr_repetitions),
                    onTextSelected = {createExerciseViewModel.onEvent(CreateExerciseUIEvent.NrRepetitionsChanged(it))},
                    fieldForNumbers = true
                )
                Button(modifier = Modifier.padding(10.dp),onClick = { pickVideoLauncher.launch("video/*") }) {
                    Text("Select Video")
                }
                uiState.videoUrl?.let { uri ->
                    VideoPlayer(videoUri = uri)
                }
            }        }

    }

}
