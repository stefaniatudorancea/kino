package com.example.kino.rules.doctorProfile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.app.EventBus
import com.example.kino.data.ChatData
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.data.DoctorReview
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserData
import com.example.kino.data.UserDataForDoctorList
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.chat.ChatUIEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class DoctorProfileViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var doctorProfileUIState = mutableStateOf(DoctorProfileUIState())
    var sizeReviews = mutableStateOf(0)
    var seeReviews = mutableStateOf(false)

    private val _reviews = MutableStateFlow<List<DoctorReview>>(emptyList())
    val reviews = _reviews.asStateFlow()

    private val _selectedDoctor = MutableLiveData<DoctorData?>()
    val selectedDoctor: LiveData<DoctorData?> = _selectedDoctor

    private val _currentUser = MutableLiveData<UserData?>()
    val currentUser: LiveData<UserData?> = _currentUser

    private val _currentFavDoctor = MutableLiveData<String?>()
    val currentFavDoctor: LiveData<String?> = _currentFavDoctor

    private val _currentUserForDoctorList = MutableLiveData<UserDataForDoctorList?>()
    val currentUserForDoctorList: LiveData<UserDataForDoctorList?> = _currentUserForDoctorList

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    private val _showReviewDialog = MutableStateFlow(false)
    val showReviewDialog = _showReviewDialog.asStateFlow()

    fun onEvent(event: DoctorProfileUIEvent){
        when(event){
            is DoctorProfileUIEvent.ReviewChanged -> {
                doctorProfileUIState.value = doctorProfileUIState.value.copy(
                    review = event.review
                )
            }
            is DoctorProfileUIEvent.AddReviewButtonClicked -> {
                addReview()
            }
            is DoctorProfileUIEvent.SeeReviewsTextClicked -> {
                seeReviews.value = !seeReviews.value
            }
            is DoctorProfileUIEvent.ConfirmDialogButtonClicked -> {
                dismissDialog()
                removeFromPatientList()
                viewModelScope.launch {
                    EventBus.postEvent("TriggerAction")
                }
                PostOfficeAppRouter.navigateTo(Screen.DoctorsScreen)
            }
        }
    }


    init{
        loadCurrentUser()
    }

    fun showConfirmationDialog() {
        _showDialog.value = true
    }

    fun dismissDialog() {
        _showDialog.value = false
    }

    fun showConfirmationReviewDialog() {
        _showReviewDialog.value = true
    }


    fun dismissReviewDialog() {
        _showReviewDialog.value = false
    }

    fun addReview() {
        viewModelScope.launch {
            // Obțineți datele utilizatorului curent
            val user = _currentUser.value ?: return@launch
            val doctorId = _selectedDoctor.value?.uid ?: return@launch

            val review = DoctorReview(
                firstName = user.firstName,
                lastName = user.lastName,
                textReview = doctorProfileUIState.value.review,
                timestamp = System.currentTimeMillis().toString() // Folosim timpul curent ca timestamp
            )

            // Adăugăm recenzia în sub-colecția 'reviews' a medicului specificat
            db.collection(DOCTOR_NODE)
                .document(doctorId)
                .collection("reviews")
                .add(review)
                .addOnSuccessListener {
                    dismissReviewDialog()
                    fetchExercises(doctorId)
                    Log.d("DoctorProfileViewModel", "Review added successfully")

                }
                .addOnFailureListener { e ->
                    Log.w("DoctorProfileViewModel", "Error adding review", e)
                }
        }
    }

//    fun getReviews() {
//        viewModelScope.launch {
//            val doctorId = _selectedDoctor.value?.uid ?: return@launch  // Asigură-te că avem un ID de doctor
//
//            try {
//                val reviewsList = db.collection("doctors")
//                    .document(doctorId)
//                    .collection("reviews")
//                    .get()
//                    .await()
//                    .mapNotNull { document ->
//                        document.toObject(DoctorReview::class.java)
//                    }
//                _reviews.value = reviewsList
//                sizeReviews.value = reviews.value.count()
//            } catch (e: Exception) {
//                Log.e("DoctorProfileViewModel", "Error fetching reviews", e)
//            }
//        }
//    }

    fun fetchExercises(uidDoctor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = db.collection(DOCTOR_NODE)
                    .document(uidDoctor)
                    .collection("reviews")
                    .get()
                    .await()
                if (snapshot.isEmpty) {
                    Log.d(TAG, "No exercises found")
                }
                val reviewList = snapshot.documents.mapNotNull { document ->
                    document.toObject(DoctorReview::class.java)?.apply {
                        Log.d(TAG, "Fetched exercise: $this")
                    }
                }
                _reviews.value = reviewList
                sizeReviews.value = reviews.value.size
                Log.d(TAG, "Exercises loaded successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Error fetching exercises", e)
            }
        }
    }


    fun selectDoctor(doctor: DoctorData) {
        _selectedDoctor.value = doctor
        fetchExercises(doctor.uid)
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
                    addPatientToDoctorList()
                    _currentFavDoctor.value = selectedDoctor.value?.uid
                    Log.d( "UpdateDoc", "noul current fav doctor is: ${selectedDoctor.value?.uid}")
                    viewModelScope.launch {
                        EventBus.postEvent("UpdateConversationId")
                    }
                    selectedDoctor.value?.let { it1 -> initiateChatWithDoctor(it1.uid) }
                }
                .addOnFailureListener { e ->
                    // Aici poți să tratezi cazul în care actualizarea a eșuat
                    Log.w("UpdateDoc", "Error updating document", e)
                }
        }
    }

    fun removeFromPatientList() {
        val selectedDoctor = currentUser.value?.let { it.favDoctor?.let { it1 ->
            db.collection(DOCTOR_NODE).document(
                it1
            )
        } }
        if (selectedDoctor != null) {
            // Utilizează FieldValue.arrayRemove pentru a elimina currentUser din array-ul patientsList
            selectedDoctor.update("patientsList", FieldValue.arrayRemove(currentUserForDoctorList))
                .addOnSuccessListener {
                    Log.d(TAG, "Patient successfully removed from patientsList")
                    assignFavDoctor()
                }
                .addOnFailureListener {
                    e -> Log.w(TAG, "Error removing patient from patientsList", e)
                }
        }
    }

    fun addPatientToDoctorList() {
        val selectedDoctor = selectedDoctor.value?.let { db.collection(DOCTOR_NODE).document(it.uid) }
        if (selectedDoctor != null) {
            // Utilizează FieldValue.arrayUnion pentru a adăuga noul pacient în array-ul patientsList
            selectedDoctor.update("patientsList", FieldValue.arrayUnion(currentUserForDoctorList))
                .addOnSuccessListener {
                    Log.d(TAG, "Patient successfully added to patientsList") }
                .addOnFailureListener { e -> Log.w(TAG, "Error adding patient to patientsList", e) }
        }
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
                    Log.d( "UpdateCurrentConversation", "Document successfully updatedddd!")
                    selectedDoctor.value?.let { it1 -> initiateChatWithDoctor(it1.uid) }
                }
                .addOnFailureListener { e ->
                    // Aici poți să tratezi cazul în care actualizarea a eșuat
                    Log.w("UpdateCurrentConversation", "Error updating document", e)
                }
        }

    }

    private fun loadCurrentUser() {
        val uid = auth.currentUser?.uid
        uid?.let {
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener { document ->
                val userData = document.toObject(UserData::class.java)
                _currentUser.value = userData
                val userDataForDoctorList = document.toObject(UserDataForDoctorList::class.java)
                _currentUserForDoctorList.value = userDataForDoctorList
                _currentFavDoctor.value = currentUser.value?.favDoctor
            }.addOnFailureListener { e ->
                // Handle the error
            }
        }
    }

    //functia care se apeleaza initial
    fun addFavDoctor() {
        val currentUid = auth.currentUser?.uid
        val currentUser = currentUid?.let { db.collection(USER_NODE).document(it) }
        if(currentUser != null) {
            currentUser.get().addOnSuccessListener { document ->
                val currentValue = document.getString("favDoctor")
                if (currentValue == "") {
                    assignFavDoctor()
                    //removeFromPatientList()
                } else {
                    showConfirmationDialog()
                }
            } .addOnFailureListener { exception ->
                println("Error fetching document: ${exception.message}")
            }
        }
    }
}