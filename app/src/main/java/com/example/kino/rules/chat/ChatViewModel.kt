package com.example.kino.rules.chat

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.data.ChatData
import com.example.kino.data.Message
import com.example.kino.data.USER_NODE
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
import java.security.MessageDigest
import javax.inject.Inject

class ChatViewModel: ViewModel() {
    private val TAG = ChatViewModel::class.simpleName
    var chatUIState = mutableStateOf(ChatUIState())

    var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private val _chatId = MutableLiveData<String?>()
    val chatId: MutableLiveData<String?> = _chatId

    private val _currentConversation = MutableLiveData<String?>()
    val currentConversation: MutableLiveData<String?> = _currentConversation

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val database = FirebaseDatabase.getInstance().reference
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
        getCurrentConversation()
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


    private fun setupRealtimeUpdates() {
        // Listener pentru mesaje noi sau schimbate
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(Message::class.java)?.let { message ->
                    val updatedMessages = _messages.value.toMutableList().apply {
                        add(message)
                    }.sortedBy { it.timestamp }
                    _messages.value = updatedMessages
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    val updatedMessages = _messages.value.toMutableList().apply {
                        val index = indexOfFirst { it.messageId == it.messageId }
                        if (index != -1) set(index, it)
                    }
                    _messages.value = updatedMessages
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot.getValue(Message::class.java)?.let { message ->
                    val updatedMessages = _messages.value.toMutableList().apply {
                        removeAll { it.messageId == message.messageId }
                    }
                    _messages.value = updatedMessages
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.toException()}")
            }
        }
        messagesRef.addChildEventListener(childEventListener as ChildEventListener)
    }



    override fun onCleared() {
        super.onCleared()
        // Îndepărtăm listener-ul atunci când ViewModel-ul este distrus
        messagesRef?.removeEventListener(childEventListener!!)
    }

    private fun updateMessageList(message: Message){
        val currentList = _messages.value ?: emptyList()
        val updatedList = currentList + message
        _messages.value = updatedList
    }

    private fun getCurrentConversation(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection(USER_NODE).document(uid).get().addOnSuccessListener { document ->
                _currentConversation.value = document.getString("currentConversation")
                Log.d(ContentValues.TAG, "conversatia curenta: ${_currentConversation.value}")
                setupMessageListener()
            }
        }
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, chatUIState.value.toString())
    }

}