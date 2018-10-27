package com.shinwan2.postmaker.core.di

import com.shinwan2.postmaker.core.annotation.CoreScope
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.Component

@Component(
    modules = [
        RemoteModule::class,
        SchedulerModule::class
    ]
)
@CoreScope
interface CoreComponent {
    fun authenticationService(): AuthenticationService
    fun userService(): UserService
    fun postService(): PostService
    fun schedulerManager(): SchedulerManager
}