package com.example.kino.rules.feedbackRoutine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.app.EventBus
import com.example.kino.data.DoctorData
import com.example.kino.data.FeedbackData
import com.example.kino.data.RoutinePatientData
import com.example.kino.data.RoutinePatientExerciseData
import com.example.kino.data.USER_NODE
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedbackRoutineViewModel: ViewModel() {

    private val _routineFeedbacks = MutableLiveData<List<FeedbackData>>()
    val routineFeedbacks: LiveData<List<FeedbackData>> = _routineFeedbacks

    private val _showFeedbackDialog = MutableStateFlow(false)
    val showFeedbackDialog = _showFeedbackDialog.asStateFlow()

    private val _currentRoutine = MutableStateFlow<RoutinePatientData?>(null)
    val currentRoutine: StateFlow<RoutinePatientData?> = _currentRoutine.asStateFlow()

    private val _isRoutineAssigned = MutableStateFlow(false)
    val isRoutineAssignet = _isRoutineAssigned.asStateFlow()

    private val _selectedPatient = MutableLiveData<String>()
    val selectedPatient: LiveData<String> = _selectedPatient

    private val db = FirebaseFirestore.getInstance()
    private val currentDoctor = FirebaseAuth.getInstance().currentUser?.uid

    fun onEvent(event: FeedbackRoutineUIEvent){
        when(event){
            is FeedbackRoutineUIEvent.SeeFeedbacksClicked ->
            {
                _showFeedbackDialog.value = !_showFeedbackDialog.value
            }
        }
    }

    init {
        viewModelScope.launch {
            EventBus.events.collect { event ->
                if (event == "AssignConversation") {
                   fetchCurrentRoutine()
                }
            }
        }
    }

    fun showFeedbackDialog() {
        _showFeedbackDialog.value = true
    }

    fun dismissFeedbackDialog() {
        _showFeedbackDialog.value = false
    }

    fun selectPatient(patientUid: String) {
        _selectedPatient.value = patientUid
        fetchCurrentRoutine()
        fetchRoutineFeedbacks()
        //fetchExercises(doctor.uid)
        //PostOfficeAppRouter.navigateTo(Screen.DoctorProfileScreen)
    }

    fun fetchRoutineFeedbacks() {
        selectedPatient.value?.let {
            db.collection(USER_NODE).document(it)
                .collection("assignedRoutines")
                .whereEqualTo("doctorUid", currentDoctor)
                .get()
                .addOnSuccessListener { snapshot ->
                    val feedbacks = mutableListOf<FeedbackData>()
                    for (document in snapshot) {
                        val routineDetails = document.get("routineDetails") as? Map<String, Any>
                        val routineName = routineDetails?.get("name") as? String ?: "Unknown Routine"
                        val feedback = document.getString("feedback") ?: "No feedback"
                        feedbacks.add(FeedbackData(feedback, routineName))
                    }
                    _routineFeedbacks.value = feedbacks
                }
                .addOnFailureListener { exception ->
                    println("Error fetching routine feedbacks: ${exception.localizedMessage}")
                }
        }
    }

    fun fetchCurrentRoutine() {
        selectedPatient.value?.let {
            db.collection(USER_NODE).document(it).get().addOnSuccessListener { userDoc ->
                val currentRoutineId = userDoc.getString("currentRoutineId")
                _isRoutineAssigned.value = currentRoutineId != null
                if (currentRoutineId != null) {
                    db.collection(USER_NODE).document(selectedPatient.value!!)
                        .collection("assignedRoutines").document(currentRoutineId)
                        .get().addOnSuccessListener { routineDoc ->
                            val routineData = routineDoc.data
                            val routineDetails = routineData?.get("routineDetails") as? Map<String, Any>
                            if (routineDetails != null) {
                                val exercisesListMap = (routineDetails["exercises"] as? List<Map<String, Any>>) ?: listOf()

                                val exercises = exercisesListMap.mapNotNull { exerciseMap ->
                                    try {
                                        RoutinePatientExerciseData(
                                            id = exerciseMap["id"] as? String ?: "",
                                            name = exerciseMap["name"] as? String ?: "",
                                            description = exerciseMap["description"] as? String,
                                            videoName = exerciseMap["videoName"] as? String,
                                            series = (exerciseMap["series"] as? Long)?.toInt() ?: 0,
                                            repetitions = (exerciseMap["repetitions"] as? Long)?.toInt() ?: 0,
                                            done = exerciseMap["done"] as? Boolean ?: false
                                        )
                                    } catch (e: Exception) {
                                        //Log.e(TAG, "Error converting exercise data: $e")
                                        null
                                    }
                                }.toMutableList()

                                val newRoutine = RoutinePatientData(
                                    id = currentRoutineId,
                                    name = routineDetails["name"] as? String ?: "",
                                    disease = routineDetails["disease"] as? String,
                                    exercises = exercises,
                                    startDate = (routineDetails["startDate"] as? Long ?: 0L),
                                    exercisesDone = (routineData["exercisesDone"] as? Long ?: 0L),
                                    creationDate = (routineDetails["creationDate"] as? Long)?.toString() ?: "Unknown Date",
                                    doctorUid = routineDetails["doctorUid"] as? String ?: ""
                                )
                                _currentRoutine.value = newRoutine
                            } else {
                                println("Routine details are missing or incorrect")
                            }
                        }.addOnFailureListener { e ->
                            println("Error loading current routine: ${e.localizedMessage}")
                        }
                } else {
                    println("No current routine ID found")
                }
            }.addOnFailureListener { e ->
                println("Error fetching user document: ${e.localizedMessage}")
            }
        }

    }
}