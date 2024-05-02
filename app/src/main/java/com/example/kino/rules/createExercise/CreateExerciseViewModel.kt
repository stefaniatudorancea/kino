package com.example.kino.rules.createExercise

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.rules.doctorSignup.DoctorSignupViewModel
import com.google.firebase.storage.FirebaseStorage

class CreateExerciseViewModel: ViewModel() {
    private val TAG = CreateExerciseViewModel::class.simpleName
    var createExerciseUIState = mutableStateOf(CreateExerciseUIState())
    private val storageReference = FirebaseStorage.getInstance().reference
    private var createInProgress = mutableStateOf(false)

    fun onEvent(event: CreateExerciseUIEvent){
        when(event){
            is CreateExerciseUIEvent.DescriptionChanged -> {
                createExerciseUIState.value = createExerciseUIState.value.copy(
                    description = event.description
                )
            }
            is CreateExerciseUIEvent.NrRepetitionsChanged -> {
                createExerciseUIState.value = createExerciseUIState.value.copy(
                    nrRepetitions = event.nrRepetitions
                )
            }
            is CreateExerciseUIEvent.CreateButtonClicked -> {
                //To do
            }
            is CreateExerciseUIEvent.VideoSelected -> {
                uploadVideo(event.uri)
            }
        }
    }


    private fun uploadVideo(uri: Uri) {
        val videoRef = storageReference.child("videos/${System.currentTimeMillis()}")
        videoRef.putFile(uri).addOnSuccessListener {
            videoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                createExerciseUIState.value = createExerciseUIState.value.copy(videoUrl = downloadUri)
            }.addOnFailureListener {
                Log.e("UploadVideo", "Error getting video URL", it)
            }
        }.addOnFailureListener {
            Log.e("UploadVideo", "Error uploading video", it)
        }
    }
}