package com.shinwan2.postmaker.core

import android.app.Application
import com.google.firebase.FirebaseApp

abstract class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}