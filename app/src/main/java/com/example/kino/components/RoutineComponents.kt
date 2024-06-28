package com.example.kino.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.RoutineData
import com.example.kino.data.RoutineDataDb
import com.example.kino.data.RoutineExerciseData
import com.example.kino.data.UserDataForDoctorList
import com.example.kino.rules.doctorProfile.DoctorProfileUIEvent
import com.example.kino.rules.doctorProfile.DoctorProfileViewModel
import com.example.kino.rules.patientsList.PatientsListViewModel
import com.example.kino.rules.routine.RoutineUIEvent
import com.example.kino.rules.routine.RoutineViewModel

@Composable
fun RoutineExerciseItem(exercise: ExerciseDataDb, routineViewModel: RoutineViewModel = viewModel()) {
    var isChecked by remember { mutableStateOf(false) }
    var series by remember { mutableStateOf("") }
    var repetitions by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(colorResource(id = R.color.cardBlue))
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = exercise.description,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        routineViewModel.onEvent(RoutineUIEvent.ExerciseCheckedChanged(exercise, isChecked))
                                      },
                    modifier = Modifier.padding(horizontal = 5.dp)
                )
            }
            if (isChecked) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(60.dp)
                            .width(120.dp),
                            //.clip(componentShapes.small),
                        shape = RoundedCornerShape(50),
                        value = series,
                        label = { Text(text = stringResource(id = R.string.series)) },
                        onValueChange = {newValue ->
                                series = newValue.filter { it.isDigit() }

                        },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(60.dp)
                            .width(120.dp),
                           // .clip(componentShapes.small)
                        shape = RoundedCornerShape(50),
                        value = repetitions,
                        label = { Text(text = stringResource(id = R.string.repetitions)) },
                        onValueChange = {newValue ->
                            repetitions = newValue.filter { it.isDigit() }

                        },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    )
                    if (series.isNotEmpty() && repetitions.isNotEmpty()) {
                        routineViewModel.onEvent(RoutineUIEvent.ExerciseDetailsChanged(
                            exercise, series.toInt(), repetitions.toInt()))
                    }
                }
                Spacer(modifier = (Modifier.height(5.dp)))
            }
        }
    }
}

@Composable
fun RoutineItem(routine: RoutineDataDb, routineViewModel: RoutineViewModel = viewModel()) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 25.dp),
        colors = CardDefaults.cardColors(colorResource(id = R.color.cardBlue))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${routine.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${routine.disease}",
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),  // Extinde Row pe toată lățimea disponibilă
                horizontalArrangement = Arrangement.Center  // Centrează conținutul pe orizontală
            ) {
                Button(
                    onClick = {
                        routineViewModel.selectRoutine(routine)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue))) {
                    Text(stringResource(id = R.string.open))
                }
            }

        }
    }
}

@Composable
fun CreateRoutineFieldComponent(
    labelValue: String,
    onTextSelected: (String) -> Unit,
    fieldForNumbers: Boolean = false
){
    val keyboardController = LocalSoftwareKeyboardController.current
    val textValue = remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 5.dp),
            //.clip(componentShapes.small),
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
fun SeeRoutineExerciseItem(ex: RoutineExerciseData, onCardClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 40.dp)
            .clickable(onClick = onCardClicked),
        colors = CardDefaults.cardColors(colorResource(id = R.color.cardBlue))
    ) {
        Column(
            modifier = Modifier.padding(7.dp)
        ) {
            Text(
                text = "${ex.exercise?.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${ex.exercise?.description}",
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                Text(
                    text = "${stringResource(id = R.string.series)}: ${ex.series}",
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${stringResource(id = R.string.repetitions)}: ${ex.repetitions}",
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DeleteRoutineDialog(routineViewModel: RoutineViewModel = viewModel()) {
    val showDialog = routineViewModel.showDeleteRoutineDialog.collectAsState()
    val routine = routineViewModel.selectedRoutine.value
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { routineViewModel.dismissDeleteRoutineDialog() },
            text = { Text(stringResource(id = R.string.delete_routine)) },
            confirmButton = {
                Button(
                    onClick = {
                        if (routine != null) {
                            routineViewModel.deleteRoutine(routine)
                            routineViewModel.dismissDeleteRoutineDialog()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue))
                ) {
                    Text(stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = { routineViewModel.dismissDeleteRoutineDialog()},
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue))
                ) {
                    Text(stringResource(id = R.string.no))
                }
            }
        )
    }
}

@Composable
fun AssignRoutineDialog(routineViewModel: RoutineViewModel = viewModel(), patientsListViewModel: PatientsListViewModel = viewModel()) {
    val showAssignDialog = routineViewModel.showAssignDialog.collectAsState()
    val patients = patientsListViewModel.patientsList.collectAsState().value
    var expanded by remember { mutableStateOf(false) }
    var selectedPatient by remember { mutableStateOf<UserDataForDoctorList?>(null) }
    val textFieldValue = remember(selectedPatient) { mutableStateOf(selectedPatient?.firstName ?: "Select patient") }

    if (showAssignDialog.value) {
        AlertDialog(
            onDismissRequest = { routineViewModel.dismissAssignDialog() },
            title = { Text(text = stringResource(id = R.string.assign_a_routine)) },
            text = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = {  },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Deschide dropdown"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .width(200.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        patients?.forEach { patient ->
                            DropdownMenuItem(
                                text = {Text(patient.firstName)},
                                onClick = {
                                    selectedPatient = patient
                                    textFieldValue.value = patient.firstName
                                    expanded = false
                                }
                            ) 
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedPatient?.let { routineViewModel.assignRoutineToPatient(it, routineViewModel.selectedRoutine.value!!) }
                    routineViewModel.dismissAssignDialog()
                                 },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue)))
                    {
                    Text(text = stringResource(id = R.string.done))
                }
            },
            dismissButton = {
                Button(onClick = { routineViewModel.dismissAssignDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.buttonBlue))
                ) {
                    Text(stringResource(id = R.string.close))
                }
            }
        )
    }
}


