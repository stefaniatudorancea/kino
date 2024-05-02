package com.example.kino

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import com.example.kino.app.PostOfficeApp

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.your_layout)
//
//        pickVideoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let { viewModel.uploadVideo(it) }
//        }
        //WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            PostOfficeApp()
        }
    }
}