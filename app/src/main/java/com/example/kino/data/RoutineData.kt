package com.example.kino.data

data class RoutineData(
    var name: String = "",
    var disease: String = "",
    var exercise: MutableList<RoutineExerciseData> = mutableListOf()
)

data class RoutineDataDb(
    var id: String = "",
    var name: String = "",
    var disease: String = "",
    var exercise: MutableList<RoutineExerciseData> = mutableListOf()
)

data class RoutineExerciseData(
    var exercise: ExerciseDataDb? = null,
    var series: Int = 0,
    var repetitions: Int = 0
)