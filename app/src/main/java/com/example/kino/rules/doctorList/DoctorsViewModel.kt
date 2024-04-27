package com.example.kino.rules.doctorList

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.app.EventBus
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserData
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DoctorsViewModel: ViewModel() {

    private val _doctorsList = MutableLiveData<List<DoctorData>>()
    val doctorsList: LiveData<List<DoctorData>> = _doctorsList

    private val _favDoctorUid = MutableLiveData<String?>()
    val favDoctorUid: LiveData<String?> = _favDoctorUid

    private val _favDoctor = MutableLiveData<DoctorData?>()
    val favDoctor: LiveData<DoctorData?> = _favDoctor

    private val dbb = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        fetchUsers()
        viewModelScope.launch {
            EventBus.events.collect { event ->
                if (event == "TriggerAction") {
                    fetchUsers()
                }
            }
        }
    }

    fun fetchUsers() {
        FirebaseFirestore.getInstance().collection(DOCTOR_NODE)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MainViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }
                val userList = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(DoctorData::class.java)
                } ?: emptyList()
                _doctorsList.value = userList
                Log.d(TAG, "am luat userii")
                loadFavDoctorUid()
            }

    }

    private fun loadFavDoctorUid(){
        val uid = auth.currentUser?.uid
        if (uid != null && uid != "") {
            dbb.collection(USER_NODE).document(uid).get().addOnSuccessListener { document ->
                _favDoctorUid.value = document.getString("favDoctor")
                Log.d(TAG, "am luat uid: ${_favDoctorUid.value}")
                loadFavDoctor()
            }
        }
    }

    private fun loadFavDoctor() {
        favDoctorUid.value?.let { it ->
            if(it != "") {
                dbb.collection(DOCTOR_NODE).document(it).get().addOnSuccessListener { document ->
                    val userData = document.toObject(DoctorData::class.java)
                    _favDoctor.value = userData // Aici userData este setat în LiveData
                    Log.d(TAG, "am luat mediculȘ ${favDoctor.value?.email}")
                    removeFavDoctor()
                }.addOnFailureListener { e ->
                    Log.e("loadCurrentUser", "Error loading user data", e)
                }
            }
        }
    }


    fun removeFavDoctor() {
        val favDoctor = _favDoctor.value
        _doctorsList.value?.let { users ->
            val mutableUsers = users.toMutableList()
            mutableUsers.removeIf { it == favDoctor }
            _doctorsList.value = mutableUsers
            Log.d(TAG, "am sters medicul")

        }
    }

}