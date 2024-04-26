package com.example.kino.rules.doctorProfile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.data.ChatData
import com.example.kino.data.DoctorData
import com.example.kino.data.USER_NODE
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

class DoctorProfileViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _selectedDoctor = MutableLiveData<DoctorData?>()
    val selectedDoctor: LiveData<DoctorData?> = _selectedDoctor

    private val _patientUid = MutableLiveData<String?>()
    val patientUid: LiveData<String?> = _patientUid

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    fun showConfirmationDialog() {
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
    }

    fun selectDoctor(doctor: DoctorData) {
        _selectedDoctor.value = doctor
        PostOfficeAppRouter.navigateTo(Screen.DoctorProfileScreen)
    }

    fun assignFavDoctor() {
        val currentUid = auth.currentUser?.uid
        val currentUser = currentUid?.let { db.collection(USER_NODE).document(it) }
        if (currentUser != null) {
            currentUser.update("favDoctor", selectedDoctor.value?.uid)
                .addOnSuccessListener {
                    // Aici poți să tratezi cazul în care actualizarea a reușit
                    Log.d( "UpdateDoc", "Document successfully updated!")
                    selectedDoctor.value?.let { it1 -> initiateChatWithDoctor(it1.uid) }
                }
                .addOnFailureListener { e ->
                    // Aici poți să tratezi cazul în care actualizarea a eșuat
                    Log.w("UpdateDoc", "Error updating document", e)
                }
        }
        dismissDialog()
    }

    fun generateConversationId(patientUid: String, doctorUid: String): String {
        val combinedId = if (doctorUid > patientUid) "$doctorUid-$patientUid" else "$patientUid-$doctorUid"
        return hashString("SHA-256", combinedId)
    }

    fun hashString(type: String, input: String): String {
        val bytes = MessageDigest
            .getInstance(type)
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun initiateChatWithDoctor(doctorUid: String) {
        var patientUid = FirebaseAuth.getInstance().currentUser!!.uid
        val database = FirebaseDatabase.getInstance().reference
        val conversationId = generateConversationId(doctorUid, patientUid)

        val conversationsRef = database.child("conversations").child(conversationId)
        conversationsRef.get().addOnSuccessListener { snapshot ->
            updateCurrentConversation(conversationId)
            if (!snapshot.exists()) {
                val conversationData = ChatData(conversationId, patientUid, doctorUid, null)
                conversationsRef.setValue(conversationData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Conversația a fost creată cu succes
                        //openChat(conversationId) // O funcție pentru a deschide interfața de chat
                    } else {
                        // Tratează eroarea
                        println("Eroare la crearea conversației.")
                    }
                }
            } else {
                // Conversația există, deschidem chat-ul
                //openChat(conversationId)
            }
        }.addOnFailureListener {
            // Tratează erorile de ascultare
            println("Eroare la accesarea bazei de date.")
        }
    }

    fun updateCurrentConversation(conversationId: String){
        val currentUid = auth.currentUser?.uid
        val currentUser = currentUid?.let { db.collection(USER_NODE).document(it) }
        if (currentUser != null) {
            currentUser.update("currentConversation", conversationId)
                .addOnSuccessListener {
                    // Aici poți să tratezi cazul în care actualizarea a reușit
                    Log.d( "UpdateCurrentConversation", "Document successfully updated!")
                    selectedDoctor.value?.let { it1 -> initiateChatWithDoctor(it1.uid) }
                }
                .addOnFailureListener { e ->
                    // Aici poți să tratezi cazul în care actualizarea a eșuat
                    Log.w("UpdateCurrentConversation", "Error updating document", e)
                }
        }
        dismissDialog()
    }

    fun addFavDoctor() {
        val currentUid = auth.currentUser?.uid
        val currentUser = currentUid?.let { db.collection(USER_NODE).document(it) }
        if(currentUser != null) {
            currentUser.get().addOnSuccessListener { document ->
                val currentValue = document.getString("favDoctor")
                if (currentValue == "") {
                    assignFavDoctor()
                } else {
                    showConfirmationDialog()
                }
            } .addOnFailureListener { exception ->
                println("Error fetching document: ${exception.message}")
            }
        }
    }
}