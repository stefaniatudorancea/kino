package com.example.kino.screens.doctorScreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.CreateExFieldComponent
import com.example.kino.components.VideoPlayer
import com.example.kino.rules.exercise.ExerciseUIEvent
import com.example.kino.rules.exercise.ExerciseViewModel
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun CreateExerciseScreen(exerciseViewModel: ExerciseViewModel = viewModel())
{
    val uiState = exerciseViewModel.createExerciseUIState.value
    val pickVideoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { exerciseViewModel.onEvent(ExerciseUIEvent.VideoSelected(it)) }
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
            LazyColumn(modifier = Modifier.fillMaxSize()){
                item{
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
                            labelValue = stringResource(id = R.string.exercise_name),
                            onTextSelected = {exerciseViewModel.onEvent(ExerciseUIEvent.NameChanged(it))},
                            fieldForNumbers = false
                        )
                        CreateExFieldComponent(
                            labelValue = stringResource(id = R.string.exercise_description),
                            onTextSelected = {exerciseViewModel.onEvent(ExerciseUIEvent.DescriptionChanged(it))},
                            fieldForNumbers = false
                        )
                        IconButton(onClick = {
                            pickVideoLauncher.launch("video/*")
                        }) {
                            Icon(painter = painterResource(id = R.drawable.video_plus), contentDescription = "add video")
                        }
                        exerciseViewModel.createExerciseUIState.value.videoUri?.let { uri ->
                            VideoPlayer(
                               uri = uri.toString()
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ButtonComponent(
                                value = stringResource(id = R.string.add),
                                onButtonClicked = { exerciseViewModel.onEvent(ExerciseUIEvent.CreateButtonClicked) },
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        colorResource(id = R.color.buttonBlue),
                                        colorResource(id = R.color.buttonBlue),
                                    )
                                ),
                                imageVector = null,
                                isEnabled = true
                            )
                        }
                    }
                }
            }
        }
    }
}
