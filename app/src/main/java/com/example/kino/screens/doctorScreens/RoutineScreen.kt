package com.example.kino.screens.doctorScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.kino.components.AssignRoutineDialog
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.DeleteRoutineDialog
import com.example.kino.components.ExerciseDetailsCard
import com.example.kino.components.NavigationAppBar
import com.example.kino.components.RoutineItem
import com.example.kino.components.SeeRoutineExerciseItem
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.exercise.ExerciseViewModel
import com.example.kino.rules.navigation.NavigationViewModel
import com.example.kino.rules.routine.RoutineViewModel


@Composable
fun RoutineScreen(navigationViewModel: NavigationViewModel = viewModel(), routineViewModel: RoutineViewModel = viewModel(), exerciseViewModel: ExerciseViewModel = viewModel()){
    val routine = routineViewModel.selectedRoutine.value
    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationItems = navigationViewModel.navigationItemsList, pageIndex = navigationViewModel.navigationItemsList[2].index)
        },
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
                AssignRoutineDialog()
                BackButton()
                DeleteRoutineDialog()
                ExerciseDetailsCard(label = stringResource(id = R.string.disease), routine?.disease)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    if (routine != null) {
                        items(routine.exercise) { ex ->
                            SeeRoutineExerciseItem(ex,
                                { ex.exercise?.let { exerciseViewModel.selectExercise(it) } })
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.width(40.dp))
                    IconButton(
                        onClick = {
                            if (routine != null) {
                                routineViewModel.showDeleteRoutineDialog()
                            } },
                        modifier = Modifier.height(45.dp)) {
                        Icon(painter = painterResource(id = R.drawable.trash), contentDescription = "Delete")
                    }
                    ButtonComponent(
                        value = stringResource(id = R.string.assign),
                        onButtonClicked = { routineViewModel.showAssignDialog()
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
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}