package com.example.kino.screens.patientScreens

import PatientRoutineViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.components.AppToolbar
import com.example.kino.components.ChatTextField
import com.example.kino.components.NavigationAppBar
import com.example.kino.components.ReceivedMessage
import com.example.kino.components.RoutineCard
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.chat.ChatUIEvent
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.navigation.NavigationViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatsScreen(navigationViewModel: NavigationViewModel = viewModel(), chatViewModel: ChatViewModel = viewModel(), patientRoutineViewModel: PatientRoutineViewModel = viewModel()) {
    Scaffold(
        bottomBar = {
            NavigationAppBar(navigationItems = navigationViewModel.navigationItemsList, pageIndex = navigationViewModel.navigationItemsList[1].index)
        },
        topBar = {
            AppToolbar(
                toolbarTitle = stringResource(id = R.string.chats),
                isDoctor = false
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
            Box(modifier = Modifier.fillMaxSize()) {
                val messages = chatViewModel.messages.collectAsState().value
                val scrollState = rememberLazyListState()
                val routine = patientRoutineViewModel.currentRoutine.collectAsState().value
                val n = routine?.exercises?.size

                LazyColumn(state = scrollState, modifier = Modifier.padding(bottom = 90.dp)) {
                    item {
                        routine?.let {
                            RoutineCard(routineName = it.name, numberOfDoneExercises = it.exercisesDone, numberOfExercises = it.exercises.size, onStart = {
                                PostOfficeAppRouter.navigateTo(Screen.PatientRoutineScreen)
                            })

                        }
                    }
                    items(messages) { message ->
                        ReceivedMessage(message = message, chatViewModel.currentUser)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 10.dp)
                        .align(Alignment.BottomCenter),
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
            if(chatViewModel.fetchChatProcess.value){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }
        }
    }
}