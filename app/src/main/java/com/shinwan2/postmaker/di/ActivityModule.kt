package com.shinwan2.postmaker.di

import com.shinwan2.postmaker.annotation.ActivityScope
import com.shinwan2.postmaker.auth.SignInActivity
import com.shinwan2.postmaker.auth.SignInActivityModule
import com.shinwan2.postmaker.auth.SignUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [SignInActivityModule::class])
    abstract fun provideSignInActivityInjector(): SignInActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun provideSignUpActivityInjector(): SignUpActivity
}