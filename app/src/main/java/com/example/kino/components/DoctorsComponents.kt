package com.example.kino.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.kino.R
import com.example.kino.data.DoctorData
import com.example.kino.rules.doctorList.DoctorsViewModel
import com.example.kino.rules.doctorProfile.DoctorProfileUIEvent
import com.example.kino.rules.doctorProfile.DoctorProfileViewModel

@Composable
fun DoctorCard(doctorsViewModel: DoctorProfileViewModel = viewModel(), user: DoctorData) {
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
            Text(
                text = "${user.firstName} ${user.lastName}",
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { doctorsViewModel.selectDoctor(user) }) {
                Icon(painter = painterResource(id = R.drawable.more_details), contentDescription = "details")
            }
        }
    }
}

@Composable
fun ImageForCard(imageUrl: String?) {
        val painter = if (!imageUrl.isNullOrEmpty()) {
            rememberImagePainter(data = imageUrl)
        } else {
            painterResource(id = R.drawable.default_profile) // O imagine default din resurse
        }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape),
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun FavDoctorDialog(doctorViewModel: DoctorProfileViewModel = viewModel()) {
    val showDialog = doctorViewModel.showDialog.collectAsState()
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { doctorViewModel.dismissDialog() },
            title = { Text("Confirmare schimbare") },
            text = { Text("Doriți să schimbați medicul favorit actual?") },
            confirmButton = {
                Button(onClick = { doctorViewModel.onEvent(DoctorProfileUIEvent.ConfirmDialogButtonClicked) }) {
                    Text("Da")
                }
            },
            dismissButton = {
                Button(onClick = { doctorViewModel.dismissDialog() }) {
                    Text("Nu")
                }
            }
        )
    }
}


