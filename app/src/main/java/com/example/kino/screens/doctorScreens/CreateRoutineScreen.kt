package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.CreateExFieldComponent
import com.example.kino.components.CreateRoutineFieldComponent
import com.example.kino.components.ExerciseItem
import com.example.kino.components.RoutineExerciseItem
import com.example.kino.rules.exercise.ExerciseUIEvent
import com.example.kino.rules.exercise.ExerciseViewModel
import com.example.kino.rules.routine.RoutineUIEvent
import com.example.kino.rules.routine.RoutineViewModel

@Composable
fun CreateRoutineScreen( exerciseViewModel: ExerciseViewModel = viewModel(), routineViewModel: RoutineViewModel = viewModel()){
    val exercises = exerciseViewModel.exercises.collectAsState().value
    Scaffold(
        topBar = {
            AppToolbar(toolbarTitle = stringResource(id = R.string.create_routine), isDoctor = true)
        },
    ) { paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
           Column {
               BackButton()
               CreateRoutineFieldComponent(
                   labelValue = stringResource(id = R.string.name),
                   onTextSelected = {routineViewModel.onEvent(RoutineUIEvent.NameChanged(it))},
                   fieldForNumbers = false
               )
               CreateRoutineFieldComponent(
                   labelValue = stringResource(id = R.string.disease),
                   onTextSelected = {routineViewModel.onEvent(RoutineUIEvent.DiseaseChanged(it))},
                   fieldForNumbers = false
               )
               Spacer(modifier = Modifier.width(10.dp))
               LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 10.dp)) {
                   items(exercises) { exercise ->
                       RoutineExerciseItem(exercise)
                   }
               }
               Spacer(modifier = Modifier.width(10.dp))
               ButtonComponent(
                   value = stringResource(id = R.string.add),
                   onButtonClicked = { routineViewModel.onEvent(RoutineUIEvent.CreateRoutineButtonClicked) },
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