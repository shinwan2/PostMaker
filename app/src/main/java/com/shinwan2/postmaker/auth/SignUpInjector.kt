package com.shinwan2.postmaker.auth

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module(includes = [SignUpViewModelModule::class])
internal class SignUpActivityModule {
    // hide other dependencies
}

@Module
internal class SignUpViewModelModule {
    @Provides
    fun provideViewModelFactory(
        signUpViewModelProvider: Provider<SignUpViewModel>
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return signUpViewModelProvider.get() as T
            }
        }
    }

    @Provides
    fun provideSignUpViewModel(
        repository: AuthenticationService,
        schedulerManager: SchedulerManager
    ): SignUpViewModel {
        return SignUpViewModel(repository, schedulerManager)
    }
}
