package com.shinwan2.postmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.shinwan2.postmaker.auth.SignInActivity
import com.shinwan2.postmaker.core.BaseApplication
import com.shinwan2.postmaker.core.di.DaggerCoreComponent
import com.shinwan2.postmaker.di.ApplicationComponent
import com.shinwan2.postmaker.di.DaggerApplicationComponent
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class MyApplication : BaseApplication(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var authenticationService: AuthenticationService

    @VisibleForTesting
    lateinit var component: ApplicationComponent

    // we will monitor auth state all the time
    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        component = DaggerApplicationComponent.builder()
            .coreComponent(DaggerCoreComponent.builder().build())
            .build()
        component.inject(this)

        authenticationService.authStateChanged()
            .subscribe { if (it == false) restartToLogin() }
    }

    override fun activityInjector() = dispatchingAndroidInjector

    private fun restartToLogin() {
        val intent = SignInActivity.intent(this)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}