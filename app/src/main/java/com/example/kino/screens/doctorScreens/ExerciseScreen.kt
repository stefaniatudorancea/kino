package com.example.kino.screens.doctorScreens

import android.content.ContentValues.TAG
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.ExerciseDetailsCard
import com.example.kino.components.SeeVideoPlayer
import com.example.kino.rules.exercise.ExerciseViewModel

@OptIn(UnstableApi::class)
@Composable
fun ExerciseScreen(exerciseViewModel: ExerciseViewModel = viewModel()){
    val exercise = exerciseViewModel.selectedExercise.value
    if (exercise != null) {
        exercise.videoName?.let { exerciseViewModel.fetchVideoUrl(it) }
    }
    Scaffold(
        topBar = {
            if (exercise != null) {
                AppToolbar(toolbarTitle = exercise.name, isDoctor = true)
            }
        },
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
                        if (exercise != null) {
                            ExerciseDetailsCard(
                                label = stringResource(id = R.string.exercise_description),
                                value = exercise.description
                            )
                            exerciseViewModel.videoUrl.value?.let {
                                Log.w(TAG, "in ui: ${it}")
                                SeeVideoPlayer(it)
                            }
                        }
                    }
                }
            }
        }
    }
}