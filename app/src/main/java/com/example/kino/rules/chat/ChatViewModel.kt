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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import javax.inject.Inject

class ChatViewModel: ViewModel() {
    private val TAG = ChatViewModel::class.simpleName
    var chatUIState = mutableStateOf(ChatUIState())
    //var allValidationsPassed = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    val chats = mutableStateOf<List<ChatData>>(listOf())
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

    fun listenForMessages(conversationId: String) {
        messagesRef = database.child("conversations").child(conversationId).child("messages")
        if (childEventListener == null) { // Creați listenerul dacă nu a fost deja creat
            childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    message?.let {
                        // Aici actualizează StateFlow-ul cu noul mesaj
                        val updatedList = _messages.value.toMutableList().apply {
                            add(it) // adaugă mesajul nou la lista existentă
                        }
                        _messages.value = updatedList // emite lista actualizată
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
                // ... alte metode suprascrise dacă este necesar
            }.also {
                // Adaugă listener la referință
                messagesRef.addChildEventListener(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        childEventListener?.let { listener ->
            messagesRef.removeEventListener(listener)
        }
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
            }
        }
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, chatUIState.value.toString())
    }

}