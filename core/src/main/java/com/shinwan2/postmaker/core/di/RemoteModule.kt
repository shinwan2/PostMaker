package com.shinwan2.postmaker.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shinwan2.postmaker.core.FirebaseAuthenticationService
import com.shinwan2.postmaker.core.FirebaseUserService
import com.shinwan2.postmaker.core.annotation.CoreScope
import com.shinwan2.postmaker.domain.UserService
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

    @Provides
    @CoreScope
    internal fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }
    }

    @Provides
    @CoreScope
    internal fun provideUserService(
        authenticationService: AuthenticationService,
        firebaseDatabase: FirebaseDatabase
    ): UserService {
        return FirebaseUserService(authenticationService, firebaseDatabase)
    }
}