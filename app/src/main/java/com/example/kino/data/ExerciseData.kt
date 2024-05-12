package com.example.kino.data

import android.net.Uri

data class ExerciseDataDb(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var videoName: String? = null
)

data class ExerciseData(
    var name: String,
    var description: String,
    var videoName: String? = null
)