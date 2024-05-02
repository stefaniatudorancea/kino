package com.example.kino.rules.chat

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.app.EventBus
import com.example.kino.data.ChatData
import com.example.kino.data.Message
import com.example.kino.data.USER_NODE
import com.example.kino.data.UserDataForDoctorList
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

class ChatViewModel: ViewModel() {
    private val TAG = ChatViewModel::class.simpleName
    var chatUIState = mutableStateOf(ChatUIState())

    var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private val _currentConversation = MutableLiveData<String?>()
    val currentConversation: MutableLiveData<String?> = _currentConversation

    private val _conversationPartener = MutableLiveData<UserDataForDoctorList>()
    val conversationPartener: MutableLiveData<UserDataForDoctorList> = _conversationPartener

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private lateinit var messagesRef: DatabaseReference
    private var childEventListener: ChildEventListener? = null

    fun onEvent(event: ChatUIEvent){
        when(event){
            is ChatUIEvent.ChatFieldChanged -> {
                chatUIState.value = chatUIState.value.copy(
                    chatField = event.chatField
                )
                printState()
            }
            is ChatUIEvent.SendButtonClicked -> {
                currentConversation.value?.let { sendMessage(it, currentUser, chatUIState.value.chatField) }
            }
        }
    }

    init{
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            getCurrentConversation(uid)
        }
        viewModelScope.launch {
            EventBus.events.collect { event ->
                if (event == "UpdateConversationId") {
                    if (uid != null) {
                        getCurrentConversation(uid)
                    }
                }
            }
        }
    }

    fun sendMessage(chatId: String, senderId: String, messageText: String) {
        val database = FirebaseDatabase.getInstance().reference
        val messagesRef = database.child("conversations").child(chatId).child("messages")

        val messageId = messagesRef.push().key // Generează un nou ID unic pentru mesaj
        val timestamp = System.currentTimeMillis().toString() // Timestamp pentru mesaj

        if (messageId != null ) {
            val message = Message(messageId, senderId, messageText, timestamp)
            messagesRef.child(messageId).setValue(message).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Mesajul a fost trimis cu succes
                    Log.d("sendMessage", "Mesaj trimis cu succes.")
                    updateLastMessage(chatId, message)
                } else {
                    // Tratarea erorii de trimitere a mesajului
                    Log.e("sendMessage", "Eroare la trimiterea mesajului.")
                }
            }
        }
    }

    fun updateLastMessage(chatId: String, lastMessage: Message) {
        val database = FirebaseDatabase.getInstance().reference
        val lastMessageRef = database.child("conversations").child(chatId).child("lastMessage")

        lastMessageRef.setValue(lastMessage).addOnSuccessListener {
            Log.d("updateSingleValue", "Ultimul mesaj a fost actualizat cu succes.")
        }.addOnFailureListener { e ->
            Log.e("updateSingleValue", "Eroare la actualizarea ultimului mesaj.", e)
        }
    }

    private fun setupMessageListener() {
        // Înlăturăm vechiul listener, indiferent dacă este sau nu `null`
        childEventListener?.let { messagesRef?.removeEventListener(it) }

        _currentConversation.value?.let { conversationId ->
            messagesRef = FirebaseDatabase.getInstance().reference.child("conversations").child(conversationId).child("messages")

            childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(Message::class.java)?.let { message ->
                        val updatedMessages = _messages.value.toMutableList().apply {
                            add(message)
                        }.sortedBy { it.timestamp }
                        _messages.value = updatedMessages
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Database error: ${error.toException()}")
                }
            }
            messagesRef.addChildEventListener(childEventListener as ChildEventListener)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Îndepărtăm listener-ul atunci când ViewModel-ul este distrus
        messagesRef?.removeEventListener(childEventListener!!)
    }

    fun getCurrentConversation(uid: String){
        FirebaseFirestore.getInstance().collection(USER_NODE).document(uid).get().addOnSuccessListener { document ->
            val newConversationId = document.getString("currentConversation")
            if (_currentConversation.value != newConversationId) {
                _currentConversation.value = newConversationId
                Log.d(ContentValues.TAG, "Conversația curentă: ${_currentConversation.value}")
                resetMessagesAndSetupListener()
            }
        }
    }

    private fun resetMessagesAndSetupListener() {
        // Îndepărtăm listener-ul vechi
        childEventListener?.let { listener ->
            messagesRef.removeEventListener(listener)
            childEventListener = null // Resetăm listener-ul
        }

        // Resetăm lista de mesaje
        _messages.value = emptyList()

        // Setup-ul noului listener pentru noua conversație
        setupMessageListener()
    }

    fun updateConversationPartener(user: UserDataForDoctorList){
        _conversationPartener.value = user
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, chatUIState.value.toString())
    }

}