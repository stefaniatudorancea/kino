package com.example.kino.rules.routine

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.app.EventBus
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.RoutineDataDb
import com.example.kino.data.RoutineExerciseData
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserDataForDoctorList
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

    private val _showAssignDialog = MutableStateFlow(false)
    val showAssignDialog = _showAssignDialog.asStateFlow()

    private val _showDeleteRoutineDialog = MutableStateFlow(false)
    val showDeleteRoutineDialog = _showDeleteRoutineDialog.asStateFlow()

    var fetchRoutinesProcess = mutableStateOf(false)

    init{
        if (currentDoctor != null) {
            fetchRoutines(currentDoctor)
        }
    }

    fun showAssignDialog() {
        _showAssignDialog.value = true
    }

    fun dismissAssignDialog() {
        _showAssignDialog.value = false
    }

    fun showDeleteRoutineDialog() {
        _showDeleteRoutineDialog.value = true
    }

    fun dismissDeleteRoutineDialog() {
        _showDeleteRoutineDialog.value = false
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
                    routineUIState.value.exercises.removeAll { it.exercise == event.exercise }
                }
            }
            is RoutineUIEvent.ExerciseDetailsChanged -> {
                routineUIState.value.exercises.find { it.exercise == event.exercise }?.also { exercise ->
                    if (event.series > 0 && event.repetitions > 0) {
                        exercise.series = event.series
                        exercise.repetitions = event.repetitions
                    } else {
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
                    fetchRoutines(doctorUid)
                    PostOfficeAppRouter.navigateTo(Screen.RoutinesListScreen)
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }

    fun deleteRoutine(routine: RoutineDataDb) {
        if (routine.id.isNotEmpty()) {
            val doctorUid = currentDoctor
            if (doctorUid != null) {
                db.collection("Doctor").document(doctorUid)
                    .collection("routines").document(routine.id)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Routine successfully deleted")
                        fetchRoutines(doctorUid)
                        PostOfficeAppRouter.navigateTo(Screen.RoutinesListScreen)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error deleting routine", e)
                    }
            } else {
                Log.e(TAG, "Error: Doctor UID is null")
            }
        } else {
            Log.e(TAG, "Error: Routine ID is empty")
        }
    }


    fun fetchRoutines(uidDoctor: String) {
        fetchRoutinesProcess.value = true
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
                                        val series = exerciseMap["series"] as? Long ?: 0  // Correctly accessing "series" from the parent map
                                        val repetitions = exerciseMap["repetitions"] as? Long ?: 0
                                        RoutineExerciseData(
                                            exercise = ExerciseDataDb(
                                                id = exerciseData?.get("id") as? String ?: "",
                                                name = exerciseData?.get("name") as? String ?: "",
                                                description = exerciseData?.get("description") as? String ?: "",
                                                videoName = exerciseData?.get("videoName") as? String
                                            ),
                                            series = series.toInt(),  // Correctly accessing "series" from the parent map
                                            repetitions = repetitions.toInt()  // Correctly accessing "repetitions" from the parent map
                                        )
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
                        fetchRoutinesProcess.value = false
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

    fun assignRoutineToPatient(patient: UserDataForDoctorList, routine: RoutineDataDb) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDocRef = db.collection(USER_NODE).document(patient.uid)
                val assignedRoutinesRef = userDocRef.collection("assignedRoutines")
                val modifiedExercises = routine.exercise.map { exercise ->
                    mapOf(
                        "id" to (exercise.exercise?.id ?: ""),
                        "name" to (exercise.exercise?.name ?: ""),
                        "description" to exercise.exercise?.description,
                        "videoName" to exercise.exercise?.videoName,
                        "repetitions" to exercise.repetitions,
                        "series" to exercise.series,
                        "done" to false,

                    )
                }

                val routineDetails = mapOf(
                    "routineDetails" to mapOf(
                        "id" to routine.id,
                        "name" to routine.name,
                        "disease" to routine.disease,
                        "exercises" to modifiedExercises,
                        "creationDate" to System.currentTimeMillis()
                    ),
                    "startDate" to System.currentTimeMillis(),
                    "doctorUid" to currentDoctor,
                    "exercisesDone" to 0
                )

                assignedRoutinesRef.add(routineDetails).addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Routine successfully assigned to patient")
                    viewModelScope.launch {
                        EventBus.postEvent("AssignConversation")
                    }
                    PostOfficeAppRouter.navigateTo(Screen.RoutinesListScreen)
                    userDocRef.update("currentRoutineId", documentReference.id)
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Error assigning routine to patient", e)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception when trying to assign routine", e)
            }
        }
    }

}