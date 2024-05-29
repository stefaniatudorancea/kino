package com.example.kino.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kino.R
import com.example.kino.app.PostOfficeApp
import com.example.kino.data.DoctorData
import com.example.kino.data.UserDataForDoctorList
import com.example.kino.navigation.PostOfficeAppRouter
import com.example.kino.navigation.Screen
import com.example.kino.rules.chat.ChatViewModel
import com.example.kino.rules.doctorProfile.DoctorProfileViewModel
import com.example.kino.rules.feedbackRoutine.FeedbackRoutineViewModel
import com.example.kino.rules.patientsList.PatientsListViewModel

@Composable
fun PatientCard(chatViewModel: ChatViewModel = viewModel(), user: UserDataForDoctorList, feedbackRoutineViewModel: FeedbackRoutineViewModel = viewModel()) {
    Card(
        colors = CardDefaults.cardColors(colorResource(id = R.color.listBlue)),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            ImageForCard(imageUrl = user.imageUrl)
            Text(
                text = "${user.firstName} ${user.lastName}",
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {
                chatViewModel.getCurrentConversation(user.uid)
                chatViewModel.updateConversationPartener(user)
                feedbackRoutineViewModel.selectPatient(user.uid)
                PostOfficeAppRouter.navigateTo(Screen.DoctorChatScreen)
            }) {
                Icon(painter = painterResource(id = R.drawable.more_details), contentDescription = "details")
            }
        }
    }
}