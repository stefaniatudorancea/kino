package com.example.kino.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.kino.R

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

