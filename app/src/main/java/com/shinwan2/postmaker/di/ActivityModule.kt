package com.shinwan2.postmaker.di

import com.shinwan2.postmaker.annotation.ActivityScope
import com.shinwan2.postmaker.auth.SignInActivity
import com.shinwan2.postmaker.auth.SignInActivityModule
import com.shinwan2.postmaker.auth.SignUpActivity
import com.shinwan2.postmaker.auth.SignUpActivityModule
import com.shinwan2.postmaker.home.HomeActivity
import com.shinwan2.postmaker.home.HomeActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeActivityModule::class])
    abstract fun provideHomeActivityInjector(): HomeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SignInActivityModule::class])
    abstract fun provideSignInActivityInjector(): SignInActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SignUpActivityModule::class])
    abstract fun provideSignUpActivityInjector(): SignUpActivity
}