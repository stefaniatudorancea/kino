import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.kino.data.ExerciseDataDb
import com.example.kino.data.RoutinePatientData
import com.example.kino.data.RoutinePatientExerciseData
import com.example.kino.data.USER_NODE
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PatientRoutineViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val TAG = PatientRoutineViewModel::class.simpleName
    private val storageReference = FirebaseStorage.getInstance().reference

    private val _currentRoutine = MutableStateFlow<RoutinePatientData?>(null)
    val currentRoutine: StateFlow<RoutinePatientData?> = _currentRoutine.asStateFlow()

    private val _selectedExercise = MutableLiveData<RoutinePatientExerciseData?>()
    val selectedExercise: LiveData<RoutinePatientExerciseData?> = _selectedExercise

    private val _videoUrl = MutableLiveData<String?>()
    val videoUrl: LiveData<String?> = _videoUrl

    init {
        fetchCurrentRoutine()
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
                                val exercisesList = (routineDetails["exercises"] as? List<Map<String, Any>>)?.mapNotNull {
                                    extractExercise(it)
                                }?.toMutableList() ?: mutableListOf()  // Convertirea listei la MutableList

                                val newRoutine = RoutinePatientData(
                                    id = currentRoutineId,
                                    name = routineDetails["name"] as? String ?: "",
                                    disease = routineDetails["disease"] as? String,
                                    exercises = exercisesList,  // Acum este MutableList
                                    startDate = routineDetails["startDate"] as? Long ?: 0L,
                                    exercisesDone = routineData["exercisesDone"] as? Int ?: 0,
                                    creationDate = routineDetails["creationDate"].toString()
                                )
                                _currentRoutine.value = newRoutine
                            } else {
                                //Log.e(TAG, "Routine details are missing or incorrect")
                            }
                        }.addOnFailureListener { e ->
                            //Log.e(TAG, "Error loading current routine", e)
                        }
                } else {
                    //Log.e(TAG, "No current routine ID found")
                }
            }.addOnFailureListener { e ->
                //Log.e(TAG, "Error fetching user document", e)
            }
        }
    }

    private fun extractExercise(exerciseMap: Map<String, Any>): RoutinePatientExerciseData {
        return RoutinePatientExerciseData(
            id = exerciseMap["id"] as String,
            name = exerciseMap["name"] as String,
            description = exerciseMap["description"] as String?,
            videoName = exerciseMap["videoName"] as String?,
            series = exerciseMap["series"] as? Int ?: 0,
            repetitions = exerciseMap["repetitions"] as? Int ?: 0,
            done = exerciseMap["done"] as? Boolean ?: false
        )
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
}
