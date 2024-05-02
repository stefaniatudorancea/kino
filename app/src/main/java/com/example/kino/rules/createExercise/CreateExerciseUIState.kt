package com.example.kino.rules.createExercise

import android.net.Uri

data class CreateExerciseUIState(
    var description: String = "",
    var nrRepetitions: String = "",
    var videoUrl: Uri? = null
)