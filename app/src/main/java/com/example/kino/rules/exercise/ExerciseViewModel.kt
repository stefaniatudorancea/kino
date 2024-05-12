package com.example.kino.rules.exercise

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.ExerciseData
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.RoutineDataDb
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class ExerciseViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentDoctor = FirebaseAuth.getInstance().currentUser?.uid
    private val TAG = ExerciseViewModel::class.simpleName
    var createExerciseUIState = mutableStateOf(ExerciseUIState())
    private val storageReference = FirebaseStorage.getInstance().reference
    private var createInProgress = mutableStateOf(false)

    private val _exercises = MutableStateFlow<List<ExerciseDataDb>>(emptyList())
    val exercises = _exercises.asStateFlow()

    private val _selectedExercise = MutableLiveData<ExerciseDataDb?>()
    val selectedExercise: LiveData<ExerciseDataDb?> = _selectedExercise

    private val _videoUrl = MutableLiveData<String?>()
    val videoUrl: LiveData<String?> = _videoUrl

    init {
        fetchExercises(currentDoctor.toString())
    }

    fun onEvent(event: ExerciseUIEvent){
        when(event){
            is ExerciseUIEvent.NameChanged -> {
                createExerciseUIState.value = createExerciseUIState.value.copy(
                    name = event.name
                )
            }
            is ExerciseUIEvent.DescriptionChanged -> {
                createExerciseUIState.value = createExerciseUIState.value.copy(
                    description = event.description
                )
            }
            is ExerciseUIEvent.CreateButtonClicked -> {
                if (currentDoctor != null) {
                    addExercise(currentDoctor)
                }
            }
            is ExerciseUIEvent.VideoSelected -> {
                viewModelScope.launch {
                    createExerciseUIState.value = createExerciseUIState.value.copy(
                        videoUri = event.uri
                    )
                    uploadVideoAndGetFileName(event.uri)
                }
            }
        }
    }

    fun addExercise(uidDoctor: String) {
        viewModelScope.launch {
            val state = createExerciseUIState.value
                val exerciseData = ExerciseData(
                    name = state.name,
                    description = state.description,
                    videoName = state.videoName
                )
                try {
                    createInProgress.value = true
                    val docRef = db.collection(DOCTOR_NODE).document(uidDoctor).collection("exercises")
                    docRef.add(exerciseData).await()
                    Log.d(TAG, "Exercise written with ID")
                    fetchExercises(uidDoctor)
                    clearExerciseCreationState()
                    PostOfficeAppRouter.navigateTo(Screen.ExercisesListScreen)
                } catch (e: Exception) {
                    Log.w(TAG, "Error adding exercise", e)
                } finally {
                    createInProgress.value = false
                }
        }
    }

    fun fetchVideoUrl(videoName: String) {
        val videoRef = storageReference.child("videos/$videoName")
        videoRef.downloadUrl.addOnSuccessListener { uri ->
            if (uri != null) {
                _videoUrl.value = uri.toString()
                Log.w("ExerciseViewModel", "video url is ${videoUrl.value}")
            } else {
                Log.w("ExerciseViewModel", "Received null URI for video")
                _videoUrl.value = "Default or error URL here"
            }
        }.addOnFailureListener {
            Log.w("ExerciseViewModel", "Error fetching video URL", it)
            _videoUrl.value = "Default or error URL here"  // Set a default or error value in case of failure
        }
    }

    fun fetchExercises(uidDoctor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = db.collection("Doctor")
                    .document(uidDoctor)
                    .collection("exercises")
                    .get()
                    .await()
                if (snapshot.isEmpty) {
                    Log.d(TAG, "No exercises found")
                }
                val exercisesList = snapshot.documents.mapNotNull { document ->
                    document.toObject(ExerciseDataDb::class.java)?.apply {
                        id = document.id // Aici setăm ID-ul documentului ca ID în obiectul ExerciseData
                        Log.d(TAG, "Fetched exercise: $this")
                    }
                }
                _exercises.value = exercisesList
                Log.d(TAG, "Exercises loaded successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Error fetching exercises", e)
            }
        }
    }

    fun selectExercise(exercise: ExerciseDataDb){
        _selectedExercise.value = exercise
        exercise.videoName?.let { loadVideoUrl(it) }
        PostOfficeAppRouter.navigateTo(Screen.ExerciseScreen)
    }

    fun getVideoUrlFromFirebaseStorage(fileName: String, onComplete: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("videos/$fileName")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onComplete(uri.toString())
        }.addOnFailureListener {
            onComplete(null)
        }
    }

    fun loadVideoUrl(fileName: String) {
        getVideoUrlFromFirebaseStorage(fileName) { url ->
            _videoUrl.postValue(url)
        }
    }

    fun clearExerciseCreationState() {
        createExerciseUIState.value = ExerciseUIState()
        _videoUrl.postValue(null)
    }

    suspend fun uploadVideoAndGetFileName(uri: Uri){
        val fileName = uri.lastPathSegment ?: "default_video_name.mp4"
        val videoRef = storageReference.child("videos/$fileName")

        try {
            createExerciseUIState.value = createExerciseUIState.value.copy(
                videoName = fileName)
            videoRef.putFile(uri).await()
        } catch (e: Exception) {
            Log.w(TAG, "Error uploading video", e)
            throw e
        }
    }
}