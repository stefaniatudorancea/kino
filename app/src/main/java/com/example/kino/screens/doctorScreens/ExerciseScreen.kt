package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.DeleteExerciseDialog
import com.example.kino.components.ExerciseDetailsCard
import com.example.kino.components.SeeVideoPlayer
import com.example.kino.rules.exercise.ExerciseViewModel

@Composable
fun ExerciseScreen(exerciseViewModel: ExerciseViewModel = viewModel()){
    val exercise = exerciseViewModel.selectedExercise.value
    val videoUrl = exerciseViewModel.videoUrl.observeAsState()
    Scaffold(
        topBar = {
            if (exercise != null) {
                AppToolbar(toolbarTitle = exercise.name, isDoctor = true)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)  // Ajustează padding-ul după preferințe
            ) {
                IconButton(
                    onClick = {
                        exerciseViewModel.showDeleteExerciseDialog()
                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .height(45.dp)  // Specifică înălțimea butonului
                ) {
                    Icon(painter = painterResource(id = R.drawable.trash), contentDescription = "Delete")
                }
            }
        }
    ){
            paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BackButton()
                        DeleteExerciseDialog()
                        if (exercise != null) {
                            ExerciseDetailsCard(
                                label = stringResource(id = R.string.exercise_description),
                                value = exercise.description
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            videoUrl.value?.let {
                                SeeVideoPlayer(it)
                            }
                        }
                    }
                }
            }
        }
    }
}