package com.example.kino.rules.exercise

import android.net.Uri

sealed class ExerciseUIEvent {
    data class NameChanged(val name:String) : ExerciseUIEvent()
    data class DescriptionChanged(val description:String) : ExerciseUIEvent()
    data class VideoSelected(val uri: Uri) : ExerciseUIEvent()
    object CreateButtonClicked: ExerciseUIEvent()
}