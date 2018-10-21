package com.shinwan2.postmaker.core.di

import com.google.firebase.auth.FirebaseAuth
import com.shinwan2.postmaker.core.FirebaseAuthenticationService
import com.shinwan2.postmaker.core.annotation.CoreScope
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.Module
import dagger.Provides

@Module
internal class RemoteModule {
    @Provides
    @CoreScope
    internal fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @CoreScope
    internal fun provideAuthenticationService(firebaseAuth: FirebaseAuth): AuthenticationService {
        return FirebaseAuthenticationService(firebaseAuth)
    }
}