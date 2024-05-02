package com.example.kino.screens.doctorScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.app.PostOfficeApp
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.ChatTextField
import com.example.kino.components.ReceivedMessage
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.chat.ChatUIEvent
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.navigation.NavigationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorChatScreen(navigationViewModel: NavigationViewModel = viewModel(), chatViewModel: ChatViewModel = viewModel()){
    val conversationPartener = chatViewModel.conversationPartener.value
    Scaffold(
        topBar = {
            AppToolbar(
                toolbarTitle = if(conversationPartener != null) "${conversationPartener.firstName} ${conversationPartener.lastName}" else "Routin3e", //Numele pacientului
                isDoctor = true,
                chatScreen = true,
                photoUrl = conversationPartener?.imageUrl
            )
        },
    ){
        paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ){
            Column(modifier = Modifier.fillMaxSize()) {
                val messages = chatViewModel.messages.collectAsState().value
                val scrollState = rememberLazyListState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(horizontal = 0.dp, vertical = 10.dp)
                        .padding(top = 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    BackButton()
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(horizontal = 0.dp, vertical = 10.dp)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonComponent(
                        value = stringResource(id = R.string.assign_a_routine),
                        onButtonClicked = { PostOfficeAppRouter.navigateTo(Screen.RoutinesScreen) },
                        brush = Brush.horizontalGradient(
                            listOf(
                                colorResource(id = R.color.buttonBlue),
                                colorResource(id = R.color.buttonBlue),
                            )
                        ),
                        imageVector = null,
                        isEnabled = true
                    )
                }
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 64.dp))
                {
                    items(messages) { message ->
                        ReceivedMessage(message = message, chatViewModel.currentUser)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 10.dp),
                        //.align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ){
                    ChatTextField(onTextSelected = {
                        chatViewModel.onEvent(ChatUIEvent.ChatFieldChanged(it))
                    },
                        onButtonClicked = { chatViewModel.onEvent(
                            ChatUIEvent.SendButtonClicked)},
                        isEnabled = true
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}