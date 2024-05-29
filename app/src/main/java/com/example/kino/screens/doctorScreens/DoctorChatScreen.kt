package com.example.kino.screens.doctorScreens

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.kino.R
import com.example.kino.app.EventBus
import com.example.kino.app.PostOfficeApp
import com.example.kino.components.AppToolbar
import com.example.kino.components.BackButton
import com.example.kino.components.ButtonComponent
import com.example.kino.components.ChatTextField
import com.example.kino.components.ClickableFeedbackTextComponent
import com.example.kino.components.ClickableReviewTextComponent
import com.example.kino.components.FeedbackRoutineDialog
import com.example.kino.components.FeedbackRoutineList
import com.example.kino.components.ReceivedMessage
import com.example.kino.components.RoutineDoctorCard
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.chat.ChatUIEvent
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.doctorProfile.DoctorProfileUIEvent
import com.example.kino.rules.feedbackRoutine.FeedbackRoutineUIEvent
import com.example.kino.rules.feedbackRoutine.FeedbackRoutineViewModel
import com.example.kino.rules.navigation.NavigationViewModel
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorChatScreen(feedbackRoutineViewModel: FeedbackRoutineViewModel = viewModel(), chatViewModel: ChatViewModel = viewModel()){
    val conversationPartener = chatViewModel.conversationPartener.value
    val routineAssignated = feedbackRoutineViewModel.currentRoutine.collectAsState().value

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
                val viewFeedbackDialog = feedbackRoutineViewModel.showFeedbackDialog.collectAsState().value
                val feedbacks = feedbackRoutineViewModel.routineFeedbacks.value
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
                feedbacks?.let {
                    ClickableFeedbackTextComponent(
                        onTextSelected = {
                            feedbackRoutineViewModel.onEvent(FeedbackRoutineUIEvent.SeeFeedbacksClicked)
                        }
                    )
                    FeedbackRoutineDialog(feedbacks, viewFeedbackDialog,
                        { feedbackRoutineViewModel.dismissFeedbackDialog() })
                }
                if(feedbackRoutineViewModel.isRoutineAssignet.collectAsState().value){
                    if (routineAssignated != null) {
                        RoutineDoctorCard(routineAssignated.name, routineAssignated.exercisesDone, routineAssignated.exercises.size)
                    }
                }else{
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
                            onButtonClicked = { PostOfficeAppRouter.navigateTo(Screen.RoutinesListScreen) },
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