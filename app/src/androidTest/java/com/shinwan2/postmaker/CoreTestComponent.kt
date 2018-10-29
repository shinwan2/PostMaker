package com.shinwan2.postmaker

import com.shinwan2.postmaker.core.di.CoreComponent
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [AsyncTaskSchedulerModule::class])
internal interface CoreTestComponent : CoreComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun authenticationService(authenticationService: AuthenticationService): Builder
        @BindsInstance
        fun postService(postService: PostService): Builder
        @BindsInstance
        fun userService(userService: UserService): Builder
        fun build(): CoreTestComponent
    }
}

@Module
internal class AsyncTaskSchedulerModule {
    @Provides
    fun provideSchedulerManager(): SchedulerManager {
        return AsyncTaskSchedulerManager()
    }
}