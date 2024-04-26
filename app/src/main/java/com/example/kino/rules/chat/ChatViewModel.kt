package com.example.kino.rules.chat

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kino.data.ChatData
import com.example.kino.data.Message
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class ChatViewModel: ViewModel() {

    private val TAG = ChatViewModel::class.simpleName
    var chatUIState = mutableStateOf(ChatUIState())
    //var allValidationsPassed = mutableStateOf(false)
    var inProcessChats = mutableStateOf(false)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private val _chatId = MutableLiveData<String?>()
    val chatId: MutableLiveData<String?> = _chatId

    private val _messages = MutableLiveData<List<Message?>>()
    val messages: MutableLiveData<List<Message?>> = _messages

    fun onEvent(event: ChatUIEvent){
        when(event){
            is ChatUIEvent.ChatFieldChanged -> {
                chatUIState.value = chatUIState.value.copy(
                    chatField = event.chatField
                )
                printState()
            }
            is ChatUIEvent.SendButtonClicked -> {
                chatId.value?.let { sendMessage(it, currentUser, chatUIState.value.chatField) }
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
    fun listenForMessages(conversationId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val messagesRef = database.child("conversations").child(conversationId).child("messages")

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    // Aici actualizează UI-ul cu noul mesaj
                    updateMessageList(message)
                    Log.d("listenForMessages", "Mesaj nou: ${it.text}")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("listenForMessages", "Eroare la ascultarea mesajelor: ${error.message}")
            }
        })
    }
    private fun updateMessageList(message: Message){
        val currentList = _messages.value ?: emptyList()
        val updatedList = currentList + message
        _messages.value = updatedList
    }
    fun openChat(conversationId: String) {
        _chatId.value = conversationId
        PostOfficeAppRouter.navigateTo(Screen.ChatsScreen)
    }
    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, chatUIState.value.toString())
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
}