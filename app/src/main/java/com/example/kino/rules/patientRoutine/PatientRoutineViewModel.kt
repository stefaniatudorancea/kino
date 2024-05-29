import android.content.ContentValues
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.Log
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.RoutinePatientData
import com.example.kino.data.RoutinePatientExerciseData
import com.example.kino.data.USER_NODE
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

class PatientRoutineViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val storageReference = FirebaseStorage.getInstance().reference

    private val _currentRoutine = MutableStateFlow<RoutinePatientData?>(null)
    val currentRoutine: StateFlow<RoutinePatientData?> = _currentRoutine.asStateFlow()

    private val _selectedExercise = MutableLiveData<RoutinePatientExerciseData?>()
    val selectedExercise: LiveData<RoutinePatientExerciseData?> = _selectedExercise

    private val _videoUrl = MutableLiveData<String?>()
    val videoUrl: LiveData<String?> = _videoUrl

    private val _favDoctorUid = MutableLiveData<String?>()
    val favDoctorUid: LiveData<String?> = _favDoctorUid

    init {
        loadFavDoctorUid()
    }

    private fun loadFavDoctorUid(){
        if (userId != null && userId != "") {
            db.collection(USER_NODE).document(userId).get().addOnSuccessListener { document ->
                _favDoctorUid.value = document.getString("favDoctor")
                fetchCurrentRoutine()
                android.util.Log.d(ContentValues.TAG, "am luat uid: ${_favDoctorUid.value}")
            }
        }
    }

    private fun fetchCurrentRoutine() {
        userId?.let { uid ->
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener { userDoc ->
                val currentRoutineId = userDoc.getString("currentRoutineId")
                if (currentRoutineId != null) {
                    db.collection(USER_NODE).document(uid)
                        .collection("assignedRoutines").document(currentRoutineId)
                        .get().addOnSuccessListener { routineDoc ->
                            val routineData = routineDoc.data
                            val routineDetails = routineData?.get("routineDetails") as? Map<String, Any>
                            if (routineDetails != null) {
                                // Assuming 'exercises' is a list of maps directly under 'routineDetails'
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

    fun selectExercise(exercise: RoutinePatientExerciseData){
        _selectedExercise.value = exercise
        exercise.videoName?.let { loadVideoUrl(it) }
        PostOfficeAppRouter.navigateTo(Screen.PatientExerciseScreen)
    }

    private fun loadVideoUrl(fileName: String) {
        getVideoUrlFromFirebaseStorage(fileName) { url ->
            _videoUrl.postValue(url)
        }
    }

    private fun getVideoUrlFromFirebaseStorage(fileName: String, onComplete: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("videos/$fileName")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onComplete(uri.toString())
        }.addOnFailureListener {
            onComplete(null)
        }
    }

    fun fetchVideoUrl(videoName: String) {
        if (_videoUrl.value?.contains(videoName) == true) {
            return
        }

        val videoRef = storageReference.child("videos/$videoName")
        videoRef.downloadUrl.addOnSuccessListener { uri ->
            if (uri != null) {
                _videoUrl.value = uri.toString()
            } else {
                _videoUrl.value = "Default or error URL here"
            }
        }.addOnFailureListener {
            _videoUrl.value = "Default or error URL here"
        }
    }

    fun markExerciseAsDone(exercise: RoutinePatientExerciseData) {
        val userId = userId
        val currentRoutineId = currentRoutine.value?.id
        if (userId != null && currentRoutineId != null) {
            val routineDocRef = db.collection(USER_NODE).document(userId)
                .collection("assignedRoutines").document(currentRoutineId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(routineDocRef)
                val routineDetails = snapshot.get("routineDetails", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE) as Map<String, Any>
                val exercises = routineDetails["exercises"] as List<Map<String, Any>>
                var incrementDone = false

                val updatedExercises = exercises.map { exerciseMap ->
                    if (exerciseMap["id"] == exercise.id && exerciseMap["done"] as Boolean == false) { // Check if the exercise is not already marked done
                        incrementDone = true
                        exerciseMap.toMutableMap().apply { this["done"] = true }
                    } else {
                        exerciseMap
                    }
                }

                if (incrementDone) {
                    val currentExercisesDone = snapshot.getLong("exercisesDone") ?: 0
                    transaction.update(routineDocRef, "exercisesDone", currentExercisesDone + 1)
                }

                transaction.update(routineDocRef, "routineDetails.exercises", updatedExercises)
            }.addOnSuccessListener {
                fetchCurrentRoutine()
                PostOfficeAppRouter.navigateTo(Screen.PatientRoutineScreen)
            }.addOnFailureListener { e ->
                println("Failed to mark exercise as done and increment count: ${e.localizedMessage}")
            }
        }
    }

    fun addFeedbackToRoutine(feedback: String) {
        val userId = userId
        val currentRoutineId = currentRoutine.value?.id

        if (userId != null && currentRoutineId != null) {
            val routineDocRef = db.collection(USER_NODE).document(userId)
                .collection("assignedRoutinaes").document(currentRoutineId)

            routineDocRef.update("feedback", feedback)
                .addOnSuccessListener {
                    println("Feedback added successfully.")
                    clearCurrentRoutineId()
                    PostOfficeAppRouter.navigateTo(Screen.ChatsScreen)
                }
                .addOnFailureListener { e ->
                    println("Error adding feedback: ${e.localizedMessage}")
                }
        } else {
            println("User ID or Routine ID is null")
        }
    }

    fun clearCurrentRoutineId() {
        val userId = userId
        if (userId != null) {
            val userDocRef = db.collection(USER_NODE).document(userId)

            userDocRef.update("currentRoutineId", null)
                .addOnSuccessListener {
                    _currentRoutine.value = null
                    println("CurrentRoutineId cleared successfully.")
                }
                .addOnFailureListener { e ->
                    println("Error clearing currentRoutineId: ${e.localizedMessage}")
                }
        } else {
            println("User ID is null, cannot clear currentRoutineId")
        }
    }
}
