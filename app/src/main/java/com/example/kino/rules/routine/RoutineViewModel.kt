package com.example.kino.rules.routine

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.RoutineDataDb
import com.example.kino.data.RoutineExerciseData
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.login.LoginUIState
import com.example.kino.rules.login.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RoutineViewModel: ViewModel() {
    private val TAG = RoutineViewModel::class.simpleName
    var routineUIState = mutableStateOf(RoutineUIState())
    private val db = Firebase.firestore
    private val currentDoctor = FirebaseAuth.getInstance().currentUser?.uid

    private val _routines = MutableStateFlow<List<RoutineDataDb>>(emptyList())
    val routines = _routines.asStateFlow()

    private val _selectedRoutine = MutableLiveData<RoutineDataDb?>()
    val selectedRoutine: LiveData<RoutineDataDb?> = _selectedRoutine

    init{
        if (currentDoctor != null) {
            fetchRoutines(currentDoctor)
        }
    }

    fun onEvent(event: RoutineUIEvent){
        when(event){
            is RoutineUIEvent.NameChanged -> {
                routineUIState.value = routineUIState.value.copy(
                    name = event.name
                )
            }
            is RoutineUIEvent.DiseaseChanged -> {
                routineUIState.value = routineUIState.value.copy(
                    disease = event.disease
                )
            }
            is RoutineUIEvent.CreateRoutineButtonClicked -> {
                if (routineUIState.value.exercises.any { it.series > 0 && it.repetitions > 0 }) {
                    addRoutine()
                } else {
                    Log.d(TAG, "Cannot save routine: No valid exercises")
                }
            }
            is RoutineUIEvent.ExerciseCheckedChanged -> {
                if (event.isChecked) {
                    routineUIState.value.exercises.add(RoutineExerciseData(event.exercise, 0, 0))
                } else {
                    // Îl elimină dacă este debifat
                    routineUIState.value.exercises.removeAll { it.exercise == event.exercise }
                }
            }
            is RoutineUIEvent.ExerciseDetailsChanged -> {
                routineUIState.value.exercises.find { it.exercise == event.exercise }?.also { exercise ->
                    if (event.series > 0 && event.repetitions > 0) {
                        exercise.series = event.series
                        exercise.repetitions = event.repetitions
                    } else {
                        // Elimină exercițiul dacă detalii sunt invalide
                        routineUIState.value.exercises.remove(exercise)
                    }
                }
            }
            is RoutineUIEvent.OpenRoutineButtonClicked -> {
                //PostOfficeAppRouter.navigateTo(Screen.RoutineScreen)
            }
        }
    }

    private fun addRoutine() {
        val doctorUid = currentDoctor
        val routineData = routineUIState.value
        if (doctorUid != null) {
            db.collection(DOCTOR_NODE).document(doctorUid)
                .collection("routines").add(routineData)
                .addOnSuccessListener { documentReference ->
                    PostOfficeAppRouter.navigateTo(Screen.RoutinesListScreen)
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

//    fun fetchRoutines(uidDoctor: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val snapshot = db.collection("Doctor")
//                    .document(uidDoctor)
//                    .collection("routines")
//                    .get()
//                    .await()
//                if (snapshot.isEmpty) {
//                    Log.d(TAG, "No routines found")
//                }
//                val routinesList = snapshot.documents.mapNotNull { document ->
//                    document.toObject(RoutineDataDb::class.java)?.apply {
//                        id = document.id
//                        Log.d(TAG, "Fetched routines: $this")
//                    }
//                }
//                _routines.value = routinesList
//                Log.d(TAG, "Routines loaded successfully")
//            } catch (e: Exception) {
//                Log.w(TAG, "Error fetching Routines", e)
//            }
//        }
//    }
fun fetchRoutines(uidDoctor: String) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            val docRef = db.collection("Doctor").document(uidDoctor)
            docRef.collection("routines").get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val routinesList = querySnapshot.documents.mapNotNull { document ->
                        try {
                            val routineData = document.data
                            val routineId = document.id
                            val routineName = routineData?.get("name") as? String ?: ""
                            val disease = routineData?.get("disease") as? String ?: ""
                            val exercisesListMap = routineData?.get("exercises") as? List<Map<String, Any>> ?: listOf()

                            val exercises = exercisesListMap.mapNotNull { exerciseMap ->
                                try {
                                    val exerciseData = exerciseMap["exercise"] as? Map<String, Any>
                                    if (exerciseData != null) {
                                        RoutineExerciseData(
                                            exercise = ExerciseDataDb(
                                                id = exerciseData["id"] as? String ?: "",
                                                name = exerciseData["name"] as? String ?: "",
                                                description = exerciseData["description"] as? String ?: "",
                                                videoName = exerciseData["videoName"] as? String
                                            ),
                                            series = exerciseMap["series"] as? Int ?: 0,
                                            repetitions = exerciseMap["repetitions"] as? Int ?: 0
                                        )
                                    } else null
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error converting exercise data: $e")
                                    null
                                }
                            }

                            RoutineDataDb(
                                id = routineId,
                                name = routineName,
                                disease = disease,
                                exercise = exercises.toMutableList()
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting routine data: $e")
                            null
                        }
                    }
                    _routines.value = routinesList
                    Log.d(TAG, "Routines loaded successfully with exercises")
                } else {
                    Log.d(TAG, "No routines found")
                    _routines.value = listOf()
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting document", exception)
                _routines.value = listOf()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error preparing to fetch Routines", e)
        }
    }
}

    fun selectRoutine(routine: RoutineDataDb){
        _selectedRoutine.value = routine
        PostOfficeAppRouter.navigateTo(Screen.RoutineScreen)
    }
}