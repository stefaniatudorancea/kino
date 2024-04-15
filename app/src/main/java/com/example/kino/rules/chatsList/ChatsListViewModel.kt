package com.example.kino.rules.chatsList

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.data.ChatData
import com.example.kino.data.ChatListItem
import com.example.kino.data.DOCTOR_NODE
import com.example.kino.data.DoctorData
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class ChatsListViewModel: ViewModel() {
    private val dbChats = FirebaseDatabase.getInstance().getReference("chats")

    private val _chats = MutableLiveData<List<ChatData>>()
    val chats: LiveData<List<ChatData>> = _chats

    private val _isDoctor = mutableStateOf<Boolean>(false)
    val isDoctor: State<Boolean> = _isDoctor

    private val _userId = mutableStateOf<String>("")
    val userId: State<String> = _userId

    init {
        getChats()
    }
    private fun getChats() {
        dbChats.addValueEventListener(object : ValueEventListener {
            private val dbChats = FirebaseDatabase.getInstance().getReference("conversations")
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = mutableListOf<ChatData>()
                for (chatSnapshot in snapshot.children) {
                    // Preia fiecare conversație ca un obiect ChatData
                    val chat = chatSnapshot.getValue(ChatData::class.java)
                    chat?.let {
                        // Verifică dacă conversația este relevantă pentru utilizatorul curent
                        if (isRelevantChat(it)) {
                            chatList.add(it)
                        }
                    }
                }
                // Actualizează LiveData cu lista de conversații relevante
                _chats.value = chatList
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun isRelevantChat(chat: ChatData): Boolean{
        if(chat.doctor == userId.value || chat.patient == userId.value)
            return true
        return false
    }

    suspend fun UpdateChatList(chat: ChatData){
        if(isDoctor.value){
            val user: UserData? = getPatientData(chat)
            //user?.let {val chatData = ChatListItem(user.firstName, user.lastName, time)}
        } else {
            val user: DoctorData? = getDoctorData(chat)
        }

    }

    suspend fun getDoctorData(chat: ChatData): DoctorData?{
        val databaseReference = FirebaseDatabase.getInstance().getReference(DOCTOR_NODE)
        return try{
            val dataSnapshot = databaseReference.child(chat.doctor).get().await()
            if (dataSnapshot.exists()) {
                dataSnapshot.getValue(DoctorData::class.java).also { userData ->
                    Log.d("FirebaseData", "Datele utilizatorului sunt: $userData")
                }
            } else {
                Log.d("FirebaseData", "Utilizatorul nu există.")
                null
            }
        } catch (exception: Exception) {
            Log.d("FirebaseData", "Database can't be accessed: ${exception.message}")
            null
        }
    }
    suspend fun getPatientData(chat: ChatData): UserData? {
        val databaseReference = FirebaseDatabase.getInstance().getReference(USER_NODE)
        return try {
            val dataSnapshot = databaseReference.child(chat.patient).get().await()
            if (dataSnapshot.exists()) {
                dataSnapshot.getValue(UserData::class.java).also { userData ->
                    Log.d("FirebaseData", "Datele utilizatorului sunt: $userData")
                }
            } else {
                Log.d("FirebaseData", "Utilizatorul nu există.")
                null
            }
        } catch (exception: Exception) {
            Log.d("FirebaseData", "Database can't be accessed: ${exception.message}")
            null
        }
    }
}