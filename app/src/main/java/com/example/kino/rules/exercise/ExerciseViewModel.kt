package com.example.kino.rules.exercise

import android.util.Log
import androidx.compose.runtime.mutableStateOf
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
                createExerciseUIState.value = createExerciseUIState.value.copy(
                    videoUri = event.uri
                )
            }
        }
    }

    fun addExercise(uidDoctor: String){
        val state = createExerciseUIState.value
        val exerciseData = ExerciseData(
            name = state.name,
            description = state.description,
            videoUri = state.videoUri
        )
        createInProgress.value = true
        val docRef = db.collection(DOCTOR_NODE).document(uidDoctor).collection("exercises")
        docRef.add(exerciseData)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Exercise written with ID: ${documentReference.id}")
                createInProgress.value = false
                if (currentDoctor != null) {
                    fetchExercises(currentDoctor)
                }
                PostOfficeAppRouter.navigateTo(Screen.ExercisesScreen)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding exercise", e)
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

    private fun generateUniqueId(): Int {
        return Random.nextInt()
    }
}