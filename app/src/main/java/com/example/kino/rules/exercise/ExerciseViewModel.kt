package com.example.kino.rules.exercise

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.ExerciseData
import com.example.kino.data.ExerciseDataDb
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

class ExerciseViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentDoctor = FirebaseAuth.getInstance().currentUser?.uid
    val TAG = ExerciseViewModel::class.simpleName
    var createExerciseUIState = mutableStateOf(ExerciseUIState())
    private val storageReference = FirebaseStorage.getInstance().reference
    private var createInProgress = mutableStateOf(false)

    private val _exercises = MutableStateFlow<List<ExerciseDataDb>>(emptyList())
    val exercises = _exercises.asStateFlow()

    private val _selectedExercise = MutableLiveData<ExerciseDataDb?>()
    val selectedExercise: LiveData<ExerciseDataDb?> = _selectedExercise

    private val _showDeleteExerciseDialog = MutableStateFlow(false)
    val showDeleteExerciseDialog = _showDeleteExerciseDialog.asStateFlow()

    private val _videoUrl = MutableLiveData<String?>()
    val videoUrl: LiveData<String?> = _videoUrl

    private val videoUrlCache = mutableMapOf<String, String>()

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

    fun showDeleteExerciseDialog() {
        _showDeleteExerciseDialog.value = true
    }

    fun dismissDeleteExerciseDialog() {
        _showDeleteExerciseDialog.value = false
    }

    private fun addExercise(uidDoctor: String) {
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
        videoUrlCache[videoName]?.let {
            _videoUrl.value = it
            return
        }

        // Dacă URL-ul nu este în cache, solicităm de la Firebase
        val videoRef = storageReference.child("videos/$videoName")
        videoRef.downloadUrl.addOnSuccessListener { uri ->
            if (uri != null) {
                _videoUrl.value = uri.toString()
                videoUrlCache[videoName] = uri.toString()  // Adăugăm URL-ul în cache
            } else {
                _videoUrl.value = "Default or error URL here"
            }
        }.addOnFailureListener {
            _videoUrl.value = "Default or error URL here"
        }
    }

    private fun fetchExercises(uidDoctor: String) {
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
                        id = document.id
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
        exercise.videoName?.let { fetchVideoUrl(it) }
        PostOfficeAppRouter.navigateTo(Screen.ExerciseScreen)
    }

    private fun getVideoUrlFromFirebaseStorage(fileName: String, onComplete: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("videos/$fileName")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onComplete(uri.toString())
        }.addOnFailureListener {
            onComplete(null)
        }
    }

    private fun loadVideoUrl(fileName: String) {
        getVideoUrlFromFirebaseStorage(fileName) { url ->
            _videoUrl.postValue(url)
        }
    }

    fun deleteExercise(exercise: ExerciseDataDb) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (exercise.id.isNotEmpty()) {
                    db.collection("Doctor").document(currentDoctor!!)
                        .collection("exercises").document(exercise.id)
                        .delete()
                        .await()
                    Log.d(TAG, "Exercise successfully deleted")

                    if (exercise.videoName?.isNotEmpty() == true) {
                        val videoRef = storageReference.child("videos/${exercise.videoName}")
                        videoRef.delete().addOnSuccessListener {
                            Log.d(TAG, "Video successfully deleted")
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "Error deleting video", e)
                        }
                    }

                    fetchExercises(currentDoctor!!)
                    PostOfficeAppRouter.navigateTo(Screen.ExercisesListScreen)

                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting exercise", e)
            }
        }
    }


    private fun clearExerciseCreationState() {
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