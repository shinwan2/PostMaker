package com.shinwan2.postmaker.core.di

import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.UserService
import dagger.Component

@Component(
    modules = [
        RemoteModule::class,
        SchedulerModule::class
    ]
)
interface CoreComponent {
    fun authenticationService(): AuthenticationService
    fun userService(): UserService
    fun schedulerManager(): SchedulerManager
}