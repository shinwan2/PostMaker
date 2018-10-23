package com.shinwan2.postmaker.core

import android.app.Application
import com.google.firebase.FirebaseApp
import timber.log.Timber

abstract class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)
    }
}