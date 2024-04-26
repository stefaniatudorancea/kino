package com.example.kino.rules.user

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserData
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.chat.ChatUIEvent
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<UserData?>()
    val user: LiveData<UserData?> = _user

    init {
        loadCurrentUser()
    }

    fun onEvent(event: UserUIEvent){
        when(event){
            is UserUIEvent.LogoutButtonClicked -> {
                logout()
            }

        }
    }

    // Încărcarea datelor utilizatorului logat
    private fun loadCurrentUser() {
        val uid = auth.currentUser?.uid
        uid?.let {
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener { document ->
                val userData = document.toObject(UserData::class.java)
                _user.value = userData
            }.addOnFailureListener { e ->
                // Handle the error
            }
        }
    }

    fun logout() {
        val fireBaseAuth = FirebaseAuth.getInstance()
        var auth: FirebaseAuth
        auth = Firebase.auth
        val currentUser = auth.currentUser
        fireBaseAuth.signOut()
        Log.d(TAG, "current: $currentUser")
        val authStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "Inside sign out success")
                PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
            } else {
                Log.d(TAG, "Inside sign out is not complete")
            }
        }
        fireBaseAuth.addAuthStateListener(authStateListener)
    }
}