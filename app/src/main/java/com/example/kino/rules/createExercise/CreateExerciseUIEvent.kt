package com.example.kino.rules.createExercise

import android.net.Uri
import com.example.kino.rules.doctorLogin.DoctorLoginUIEvent

sealed class CreateExerciseUIEvent {
    data class DescriptionChanged(val description:String) : CreateExerciseUIEvent()
    data class NrRepetitionsChanged(val nrRepetitions:String) : CreateExerciseUIEvent()
    data class VideoSelected(val uri: Uri) : CreateExerciseUIEvent()
    object CreateButtonClicked: CreateExerciseUIEvent()
}