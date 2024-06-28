package com.example.kino.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.kino.R
import com.example.kino.data.DoctorReview
import com.example.kino.rules.doctorProfile.DoctorProfileViewModel
import com.example.kino.rules.myDoctorProfile.MyDoctorProfileViewModel

@Composable
fun ProfileImage(imageUrl: String?) {
    val painter = if (imageUrl != null) rememberImagePainter(imageUrl) {
        error(R.drawable.default_profile)  // Specifică o imagine implicită în caz de eroare
        placeholder(R.drawable.default_profile)  // Imaginea afișată în timpul încărcării
    } else {
        painterResource(id = R.drawable.default_profile)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape),
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun UserDetailsCard(label: String, value: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(50),
        //colors = CardColors(containerColor = colorResource(id = R.color.purple_200))
        ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "$label", style = MaterialTheme.typography.bodyMedium)
            Text(text = "$value", style = MaterialTheme.typography.bodyLarge)

        }
    }
}

@Composable
fun DoctorDetailsCard(label: String, value: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(50),

        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.cardBlue)
        )    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "$label", style = MaterialTheme.typography.bodyMedium)
            Text(text = "$value", style = MaterialTheme.typography.bodyLarge)

        }
    }
}

@Composable
fun DoctorsReviewList(reviews: List<DoctorReview>, myDoctorProfileViewModel: MyDoctorProfileViewModel= viewModel()) {
    val seeReviews = myDoctorProfileViewModel.seeReviews.value
    if(seeReviews){
        Column {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .heightIn(max = 250.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Column {
                    DividerTextComponent("Reviews received")
                    LazyColumn {
                        items(reviews.size) { index ->
                            DoctorReviewItem(reviews[index])
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DoctorReviewItem(review: DoctorReview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.cardBlue))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "${review.firstName} ${review.lastName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = "${review.textReview}")
            Text(text = "${convertTimestampToDate(review.timestamp.toLong())}", fontStyle = FontStyle.Italic)
        }
    }
}
