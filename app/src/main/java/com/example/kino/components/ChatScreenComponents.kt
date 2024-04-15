package com.example.kino.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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

@Composable
fun ChatTextField(
    onTextSelected: (String) -> Unit,
    onButtonClicked: () -> Unit,
    errorStatus: Boolean = false,
    isEnabled: Boolean = false
){
    var textValue = remember { mutableStateOf("") }
    OutlinedTextField(
        shape = RoundedCornerShape(50),
        value = textValue.value,
        onValueChange = {
            textValue.value = it
            onTextSelected(it)
        },
        singleLine = true,
        //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        //singleLine = true,
        //maxLines = 1,
        //isError = !errorStatus
    )
    IconButton(onClick = { onButtonClicked.invoke(); textValue.value = ""; }, enabled = isEnabled)
    {
        Icon(
            imageVector = Icons.Outlined.Send,
            contentDescription = "Send Message",
        )
    }
}
@Composable
fun ChatCard(chatViewModel: ChatViewModel = viewModel(), user: DoctorData) {
    Card(
        colors = CardDefaults.cardColors(colorResource(id = R.color.primaryPurple)),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp)
        //.border(2.dp, RoundedCornerShape(8.dp)), // Portocaliu și colțuri rotunjite
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            ImageForCard(imageUrl = user.imageUrl)
            androidx.compose.material3.Text(
                text = "${user.firstName} ${user.firstName}",
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
        }
    }
}
@Composable
fun ReceivedMessage(chatViewModel: ChatViewModel = viewModel(), message: Message){
    Card(
        colors = CardDefaults.cardColors(colorResource(id = R.color.primaryPurple)),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp)
        //.border(2.dp, RoundedCornerShape(8.dp)), // Portocaliu și colțuri rotunjite
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(8.dp)
        ) {

        }
    }
}

