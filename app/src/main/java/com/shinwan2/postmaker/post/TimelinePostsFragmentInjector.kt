package com.shinwan2.postmaker.post

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module(includes = [TimelinePostsViewModelModule::class])
internal abstract class TimelinePostsFragmentModule

@Module
internal class TimelinePostsViewModelModule {
    @Provides
    fun provideViewModelFactory(
        viewModelProvider: Provider<TimelinePostsViewModel>
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelProvider.get() as T
            }
        }
    }

    @Provides
    fun provideTimelinePostsViewModel(
        authenticationService: AuthenticationService,
        postService: PostService,
        schedulerManager: SchedulerManager
    ): TimelinePostsViewModel {
        return TimelinePostsViewModel(authenticationService, postService, schedulerManager)
    }
}
