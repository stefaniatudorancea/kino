package com.example.kino.rules.doctorList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
class DoctorsViewModel: ViewModel() {

    private val _users = MutableLiveData<List<DoctorData>>()
    val users: LiveData<List<DoctorData>> = _users

    private val _selectedDoctor = MutableLiveData<DoctorData?>()
    val selectedDoctor: LiveData<DoctorData?> = _selectedDoctor
    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        FirebaseFirestore.getInstance().collection(DOCTOR_NODE)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MainViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val userList = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(DoctorData::class.java)
                } ?: emptyList()

                _users.value = userList
            }
    }

     fun selectDoctor(doctor: DoctorData) {
        _selectedDoctor.value = doctor
        PostOfficeAppRouter.navigateTo(Screen.DoctorProfileScreen)
    }

//    private fun tryCollab() {
//
//    }
//
//    private fun verifyFavDoctor() {
//        val uid = auth.currentUser?.uid
//        uid?.let {
//            viewModelScope.launch {
//                try {
//                    val document = db.collection(USER_NODE).document(uid).get().await()
//                    val userData = document.toObject(UserData::class.java)
//                } catch (e: Exception) {
//                    // Tratează orice excepții care ar putea apărea
//                }
//            }
//        }
//    }
}