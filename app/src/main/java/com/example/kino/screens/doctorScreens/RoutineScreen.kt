package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.RoutineItem
import com.example.kino.components.SeeRoutineExerciseItem
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.exercise.ExerciseViewModel
import com.example.kino.rules.routine.RoutineViewModel


@Composable
fun RoutineScreen(routineViewModel: RoutineViewModel = viewModel(), exerciseViewModel: ExerciseViewModel = viewModel()){
    val routine = routineViewModel.selectedRoutine.value
    val exercises = routine?.exercise
    Scaffold(
        topBar = {
            if (routine != null) {
                AppToolbar(toolbarTitle = routine.name, isDoctor = true)
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
        ){
            Column {
                BackButton()
                LazyColumn(modifier = Modifier.weight(1f)) {
                    if (routine != null) {
                        items(routine.exercise) { ex ->
                            SeeRoutineExerciseItem(ex,
                                { ex.exercise?.let { exerciseViewModel.selectExercise(it) } })
                        }
                    }
                }
                ButtonComponent(
                    value = stringResource(id = R.string.assign),
                    onButtonClicked = { //TODO: assign
                         },
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