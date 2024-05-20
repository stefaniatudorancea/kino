package com.example.kino.data

data class RoutinePatientExerciseData(
    val id: String,
    val name: String,
    val description: String?,
    val videoName: String?,
    val series: Int,
    val repetitions: Int,
    val done: Boolean = false
)

data class RoutinePatientData(
    val id: String,
    val name: String,
    val disease: String?,
    val exercises: MutableList<RoutinePatientExerciseData>,
    val startDate: Long,
    val exercisesDone: Int,
    val creationDate: String
)
