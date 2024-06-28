package com.example.kino.rules.myDoctorProfile

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.data.DoctorReview
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserData
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.user.UserUIEvent
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyDoctorProfileViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableStateFlow<DoctorData?>(null)
    val user: StateFlow<DoctorData?> = _user.asStateFlow()

    private val _reviews = MutableStateFlow<List<DoctorReview>>(emptyList())
    val reviews = _reviews.asStateFlow()

    var sizeReviews = mutableStateOf(0)
    var seeReviews = mutableStateOf(false)

    init {
        loadCurrentUser()
    }

    fun seeReviewsClicked(){
        seeReviews.value = !seeReviews.value
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            uid?.let {
                fetchReviews(uid)
                try {
                    val document = db.collection(DOCTOR_NODE).document(uid).get().await()
                    val userData = document.toObject(DoctorData::class.java)
                    _user.value = userData  // Actualizăm StateFlow
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "Failed to load user data: $e")
                    _user.value = null
                }
            }
        }
    }

    fun fetchReviews(uidDoctor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = db.collection(DOCTOR_NODE)
                    .document(uidDoctor)
                    .collection("reviews")
                    .get()
                    .await()
                if (snapshot.isEmpty) {
                    Log.d(ContentValues.TAG, "No exercises found")
                }
                val reviewList = snapshot.documents.mapNotNull { document ->
                    document.toObject(DoctorReview::class.java)?.apply {
                        Log.d(ContentValues.TAG, "Fetched exercise: $this")
                    }
                }
                _reviews.value = reviewList
                sizeReviews.value = reviews.value.size
                Log.d(ContentValues.TAG, "Exercises loaded successfully")
            } catch (e: Exception) {
                Log.w(ContentValues.TAG, "Error fetching exercises", e)
            }
        }
    }

    fun logout() {
        val fireBaseAuth = FirebaseAuth.getInstance()
        var auth: FirebaseAuth
        auth = Firebase.auth
        val currentUser = auth.currentUser
        fireBaseAuth.signOut()
        Log.d(ContentValues.TAG, "current: $currentUser")
        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(ContentValues.TAG, "Inside sign out success")
                PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
            } else {
                Log.d(ContentValues.TAG, "Inside sign out is not complete")
            }
        }
        fireBaseAuth.addAuthStateListener(authStateListener)
    }

}

