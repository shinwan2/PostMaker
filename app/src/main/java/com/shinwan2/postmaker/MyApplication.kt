package com.shinwan2.postmaker

import android.app.Activity
import com.shinwan2.postmaker.core.BaseApplication
import com.shinwan2.postmaker.core.di.DaggerCoreComponent
import com.shinwan2.postmaker.di.DaggerApplicationComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class MyApplication : BaseApplication(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        DaggerApplicationComponent.builder()
            .coreComponent(DaggerCoreComponent.builder().build())
            .build()
            .inject(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}