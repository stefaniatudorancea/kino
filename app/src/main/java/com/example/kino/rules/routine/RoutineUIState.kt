package com.example.kino.rules.routine

import com.example.kino.data.RoutineExerciseData

data class RoutineUIState(
    var name: String = "",
    var disease: String = "",
    var exercises: MutableList<RoutineExerciseData> = mutableListOf()
)