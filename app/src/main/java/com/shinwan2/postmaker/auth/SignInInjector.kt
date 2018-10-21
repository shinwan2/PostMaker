package com.shinwan2.postmaker.auth

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module(includes = [ViewModelModule::class])
internal class SignInActivityModule {
    // hide other dependencies
}

@Module
internal class ViewModelModule {
    @Provides
    fun provideViewModelFactory(
        signInViewModelProvider: Provider<SignInViewModel>
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return signInViewModelProvider.get() as T
            }
        }
    }

    @Provides
    fun provideSignInViewModel(
        repository: AuthenticationService,
        schedulerManager: SchedulerManager
    ): SignInViewModel {
        return SignInViewModel(repository, schedulerManager)
    }
}
