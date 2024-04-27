package com.example.kino.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.data.DoctorData
import com.example.kino.data.Message
import com.example.kino.rules.chat.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatTextField(
    onTextSelected: (String) -> Unit,
    onButtonClicked: () -> Unit,
    isEnabled: Boolean = false
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Adaugă padding pentru a nu lipi componentele de marginile ecranului
        verticalAlignment = Alignment.CenterVertically
    ) {
        var textValue = remember { mutableStateOf("") }
        OutlinedTextField(
            shape = RoundedCornerShape(50),
            value = textValue.value,
            modifier = Modifier
                .weight(1f)  // Utilizează 'weight' pentru a ocupa tot spațiul disponibil minus butonul
                .padding(end = 8.dp),
            onValueChange = {
                textValue.value = it
                onTextSelected(it)
            },
            singleLine = true,
        )
        IconButton(
            onClick = { onButtonClicked.invoke(); textValue.value = ""; },
            enabled = isEnabled
        )
        {
            Icon(
                imageVector = Icons.Outlined.Send,
                contentDescription = "Send Message",
            )
        }
    }
}

@Composable
fun ReceivedMessage(message: Message, currentUser: String) {
    Box(
        contentAlignment = if (message.senderId == currentUser) Alignment.CenterEnd else Alignment.CenterStart,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.senderId == currentUser) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(min = 10.dp, max = 300.dp)  // Limităm lățimea cardului între 100.dp și 300.dp
                .wrapContentHeight()  // Înălțimea se adaptează la conținut
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .background(
                        color = if (message.senderId == currentUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)  // Padding intern pentru text
            )
        }
    }
}




