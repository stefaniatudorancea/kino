package com.example.kino.rules.patientsList

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.Message
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserDataForDoctorList
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.navigation.NavigationUIEvent
import com.example.kino.rules.signup.SignupUIEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientsListViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _patientsList = MutableStateFlow<List<UserDataForDoctorList>?>(null)
    val patientsList: StateFlow<List<UserDataForDoctorList>?> = _patientsList.asStateFlow()
    var fetchPatiensProcess = mutableStateOf(false)

    fun onEvent(event: PatientsListUIEvent) {
        when (event) {
            is PatientsListUIEvent.ViewChatButtonClicked -> {
                PostOfficeAppRouter.navigateTo(Screen.DoctorChatScreen)
            }
        }
    }

    init {
        loadPatientsForDoctor()
        startPatientsListener()
    }

    private fun startPatientsListener() {
        val doctorId = auth.currentUser?.uid
        doctorId?.let { id ->
            val docRef = db.collection(DOCTOR_NODE).document(id)
            docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val patientsListMap = snapshot.get("patientsList") as? List<Map<String, Any>> ?: listOf()
                    val patients = patientsListMap.mapNotNull { patientMap ->
                        try {
                            val patientDetails = patientMap["value"] as Map<String, Any>
                            UserDataForDoctorList(
                                uid = patientDetails["uid"] as? String ?: "",
                                firstName = patientDetails["firstName"] as? String ?: "",
                                lastName = patientDetails["lastName"] as? String ?: "",
                                imageUrl = patientDetails["imageUrl"] as? String
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting patient data: $e")
                            null
                        }
                    }
                    _patientsList.value = patients
                } else {
                    Log.d(TAG, "Current data: null")
                    _patientsList.value = listOf()
                }
            }
        }
    }

    fun loadPatientsForDoctor() {
        fetchPatiensProcess.value = true
        viewModelScope.launch {
            val doctorId = auth.currentUser?.uid
            doctorId?.let { id ->
                val docRef = db.collection(DOCTOR_NODE).document(id)
                docRef.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val patientsListMap = documentSnapshot.get("patientsList") as? List<Map<String, Any>> ?: listOf()
                        val patients = patientsListMap.mapNotNull { patientMap ->
                            try {
                                val patientDetails = patientMap["value"] as Map<String, Any>
                                UserDataForDoctorList(
                                    uid = patientDetails["uid"] as? String ?: "",
                                    firstName = patientDetails["firstName"] as? String ?: "",
                                    lastName = patientDetails["lastName"] as? String ?: "",
                                    imageUrl = patientDetails["imageUrl"] as? String
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Error converting patient data: $e")
                                null
                            }
                        }

                        _patientsList.value = patients
                        fetchPatiensProcess.value = false
                    } else {
                        Log.d(TAG, "Document does not exist")
                        _patientsList.value = listOf()
                    }
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting document", exception)
                    _patientsList.value = listOf()
                }
            }
        }
    }

    companion object {
        private const val TAG = "PatientsListViewModel"
    }

}