package com.example.kino.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.ChatTextField
import com.example.kino.components.NavigationAppBar
import com.example.kino.rules.chat.ChatUIEvent
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.navigation.NavigationViewModel

@Composable
fun ChatsScreen(navigationViewModel: NavigationViewModel = viewModel(), chatViewModel: ChatViewModel = viewModel()) {
    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationViewModel = navigationViewModel, pageIndex = navigationViewModel.navigationItemsList[1].index)
        },
        topBar = {
            AppToolbar(
                toolbarTitle = stringResource(id = R.string.chats)
            )
        },
    ) { paddingValues ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 0.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ){
                ChatTextField(onTextSelected = {
                    chatViewModel.onEvent(ChatUIEvent.ChatFieldChanged(it))
                },
                    errorStatus = chatViewModel.chatUIState.value.chatFieldError,
                    onButtonClicked = { chatViewModel.onEvent(
                        ChatUIEvent.SendButtonClicked)},
                    isEnabled = true
                )
                Spacer(modifier = Modifier.width(10.dp))
//              SendButton(onButtonClicked = { chatViewModel.onEvent(
//              ChatUIEvent.SendButtonClicked)}, isEnabled = true)
            }
        }
    }
}

@Preview
@Composable
fun abc(){
    ChatsScreen()
}