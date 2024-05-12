package com.example.kino.rules.exercise

import android.net.Uri

data class ExerciseUIState(
    var name: String = "",
    var description: String = "",
    var videoUri: Uri? = null,
    var videoName: String? = null
)