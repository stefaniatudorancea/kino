package com.example.kino.rules.chat

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kino.app.EventBus
import com.example.kino.data.ChatData
import com.example.kino.data.FeedbackData
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

    private val AES_KEY = "cheieSecreta1234" // Asigură-te că cheia are lungimea de 16 bytes
    private val AES_IV = ByteArray(16)

    var chatUIState = mutableStateOf(ChatUIState())

    var currentUser = FirebaseAuth.getInstance().currentUser!!.uid

    private val _currentConversation = MutableLiveData<String?>()
    val currentConversation: MutableLiveData<String?> = _currentConversation

    private val _conversationPartener = MutableLiveData<UserDataForDoctorList>()
    val conversationPartener: MutableLiveData<UserDataForDoctorList> = _conversationPartener

    private val _conversationDoctorPartener = MutableLiveData<UserDataForDoctorList>()
    val conversationDoctorPartener: MutableLiveData<UserDataForDoctorList> = _conversationDoctorPartener


    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()



    private lateinit var messagesRef: DatabaseReference
    private var childEventListener: ChildEventListener? = null
    var fetchChatProcess = mutableStateOf(false)

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
        val encryptedMessage = encryptMessage(messageText)
        val database = FirebaseDatabase.getInstance().reference
        val messagesRef = database.child("conversations").child(chatId).child("messages")
        val messageId = messagesRef.push().key
        val timestamp = System.currentTimeMillis().toString()

        if (messageId != null ) {
            val message = Message(messageId, senderId, encryptedMessage, timestamp)
            messagesRef.child(messageId).setValue(message).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("sendMessage", "Mesaj trimis cu succes.")
                    updateLastMessage(chatId, message)
                } else {
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
        fetchChatProcess.value = true
        _currentConversation.value?.let { conversationId ->
            messagesRef = FirebaseDatabase.getInstance().reference.child("conversations").child(conversationId).child("messages")

            childEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.getValue(Message::class.java)?.let { encryptedMessage ->
                        val decryptedMessageText = decryptMessage(encryptedMessage.text)
                        val decryptedMessage = encryptedMessage.copy(text = decryptedMessageText)
                        val updatedMessages = _messages.value.toMutableList().apply {
                            add(decryptedMessage)
                        }.sortedBy { it.timestamp }
                        _messages.value = updatedMessages
                        fetchChatProcess.value = false
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
        childEventListener?.let { listener ->
            messagesRef.removeEventListener(listener)
            childEventListener = null
        }
        _messages.value = emptyList()
        setupMessageListener()
    }

    fun updateConversationPartener(user: UserDataForDoctorList){
        _conversationPartener.value = user
    }

    private fun printState() {
        Log.d(TAG, "Inside_printState")
        Log.d(TAG, chatUIState.value.toString())
    }


    fun encryptMessage(message: String): String {
        val secretKey = SecretKeySpec(AES_KEY.toByteArray(charset("UTF-8")), "AES")
        val iv = IvParameterSpec(AES_IV)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

        val encrypted = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decryptMessage(encryptedMessage: String): String {
        val secretKey = SecretKeySpec(AES_KEY.toByteArray(charset("UTF-8")), "AES")
        val iv = IvParameterSpec(AES_IV)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)

        val decodedValue = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val decrypted = cipher.doFinal(decodedValue)
        return String(decrypted, Charsets.UTF_8)
    }
}