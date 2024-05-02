package com.example.kino.components

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun VideoPlayer(videoUri: Uri) {
    var isLoading by remember { mutableStateOf(true) }  // Stare inițială pentru încărcare
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator()  // Afișează spinner-ul când isLoading este true
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                VideoView(ctx).apply {
                    setVideoURI(videoUri)
                    setMediaController(MediaController(ctx).apply {
                        setAnchorView(this@apply)
                    })
                    setOnPreparedListener {
                        isLoading = false  // Ascunde spinner-ul și începe redarea
                        start()
                    }
                    setOnCompletionListener {
                        // Poți adăuga logica pentru când videoclipul se termină
                    }
                    setOnErrorListener { _, _, _ ->
                        isLoading = false  // Ascunde spinner-ul în caz de eroare
                        false
                    }
                }
            },
            update = { videoView ->
                videoView.setVideoURI(videoUri)
            }
        )
    }
}



@Composable
fun CreateExFieldComponent(
    labelValue: String,
    onTextSelected: (String) -> Unit,
    fieldForNumbers: Boolean = false
){
    val textValue = remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 5.dp)
            .clip(componentShapes.small),
        shape = RoundedCornerShape(50),
        value = textValue.value,
        label = { Text(text = labelValue) },
        onValueChange = {newValue ->
            if(!fieldForNumbers){
                textValue.value = newValue
            }else{
                textValue.value = newValue.filter { it.isDigit() }
            }
            onTextSelected(newValue)
        },
        //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        singleLine = true,
    )
}


