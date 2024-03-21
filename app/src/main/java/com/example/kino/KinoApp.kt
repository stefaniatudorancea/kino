package com.example.kino

import android.app.Application
import com.google.firebase.FirebaseApp

class KinoApp: Application(){
    override fun onCreate(){
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}