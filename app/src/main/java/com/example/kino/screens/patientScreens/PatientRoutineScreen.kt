package com.example.kino.screens.patientScreens

import PatientRoutineViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.app.PostOfficeApp
import com.example.kino.components.AppToolbar
import com.example.kino.components.AssignRoutineDialog
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.ExerciseDetailsCard
import com.example.kino.components.RoutineFeedbackComponent
import com.example.kino.components.SeePatientRoutineExerciseItem
import com.example.kino.components.SeeRoutineExerciseItem
import com.example.kino.components.componentShapes
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.doctorProfile.DoctorProfileUIEvent
import com.example.kino.rules.exercise.ExerciseViewModel
import com.example.kino.rules.routine.RoutineViewModel

@Composable
fun PatientRoutineScreen(patientRoutineViewModel: PatientRoutineViewModel = viewModel()){
    val routine = patientRoutineViewModel.currentRoutine.collectAsState().value
    var isRoutineDone = false
    if (routine != null) {
        if(routine.exercisesDone.toInt() == routine.exercises.size){
            isRoutineDone = true
        }
    }
    Scaffold(
        topBar = {
            if (routine != null) {
                AppToolbar(toolbarTitle = routine.name, isDoctor = false)
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
                ExerciseDetailsCard(label = stringResource(id = R.string.disease), routine?.disease)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    if (routine != null) {
                        items(routine.exercises) { ex ->
                            SeePatientRoutineExerciseItem(ex,
                                { ex.let { patientRoutineViewModel.selectExercise(it) } })
                        }
                    }
                }
                if(isRoutineDone){
                    var feedback: String? = null
                    RoutineFeedbackComponent(
                        onTextSelected = {
                            feedback = it
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    ButtonComponent(
                        value = stringResource(id = R.string.done),
                        onButtonClicked = {
                            PostOfficeAppRouter.navigateTo(Screen.ChatsScreen)

                            feedback?.let { patientRoutineViewModel.addFeedbackToRoutine(it)
                                patientRoutineViewModel.clearCurrentRoutineId()}
                                   },
                        brush = Brush.horizontalGradient(
                            listOf(
                                colorResource(id = R.color.primaryPurple),
                                colorResource(id = R.color.secondaryPurple),
                            )
                        ),
                        imageVector = null,
                        isEnabled = true
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}