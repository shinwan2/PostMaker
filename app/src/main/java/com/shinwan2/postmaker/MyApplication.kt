package com.shinwan2.postmaker

import android.app.Application
import com.google.firebase.FirebaseApp
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)
    }
}