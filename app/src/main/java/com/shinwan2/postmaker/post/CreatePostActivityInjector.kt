package com.shinwan2.postmaker.post

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module(includes = [CreatePostViewModelModule::class])
internal abstract class CreatePostActivityModule

@Module
internal class CreatePostViewModelModule {
    @Provides
    fun provideViewModelFactory(
        viewModelProvider: Provider<CreatePostViewModel>
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelProvider.get() as T
            }
        }
    }

    @Provides
    fun provideCreatePostViewModel(
        postService: PostService,
        schedulerManager: SchedulerManager
    ): CreatePostViewModel {
        return CreatePostViewModel(postService, schedulerManager)
    }
}
