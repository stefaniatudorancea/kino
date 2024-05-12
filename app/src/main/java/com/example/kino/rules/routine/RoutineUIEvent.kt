package com.example.kino.rules.routine

import com.example.kino.data.ExerciseData
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.RoutineExerciseData
import com.example.kino.rules.login.LoginUIEvent

sealed class RoutineUIEvent {
    data class NameChanged(val name:String) : RoutineUIEvent()
    data class DiseaseChanged(val disease:String) : RoutineUIEvent()
    object CreateRoutineButtonClicked: RoutineUIEvent()
    data class ExerciseCheckedChanged(val exercise: ExerciseDataDb, val isChecked: Boolean) : RoutineUIEvent()
    data class ExerciseDetailsChanged(val exercise: ExerciseDataDb, val series: Int, val repetitions: Int) : RoutineUIEvent()
    object OpenRoutineButtonClicked: RoutineUIEvent()
}