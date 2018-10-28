package com.shinwan2.postmaker.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shinwan2.postmaker.core.annotation.CoreScope
import com.shinwan2.postmaker.core.firebase.repository.FirebaseAuthenticationRepository
import com.shinwan2.postmaker.core.firebase.repository.FirebasePostRepository
import com.shinwan2.postmaker.core.firebase.repository.FirebaseUserRepository
import com.shinwan2.postmaker.core.firebase.service.FirebaseAuthenticationService
import com.shinwan2.postmaker.core.firebase.service.FirebasePostService
import com.shinwan2.postmaker.core.firebase.service.FirebaseUserService
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import dagger.Module
import dagger.Provides

@Module(includes = [FirebaseRepositoryModule::class])
internal class FirebaseServiceModule {
    @Provides
    @CoreScope
    internal fun provideAuthenticationService(
        authenticationRepository: FirebaseAuthenticationRepository
    ): AuthenticationService {
        return FirebaseAuthenticationService(authenticationRepository)
    }

    @Provides
    @CoreScope
    internal fun provideUserService(
        authenticationRepository: FirebaseAuthenticationRepository,
        userRepository: FirebaseUserRepository
    ): UserService {
        return FirebaseUserService(authenticationRepository, userRepository)
    }

    @Provides
    @CoreScope
    internal fun providePostService(
        authenticationRepository: FirebaseAuthenticationRepository,
        postRepository: FirebasePostRepository,
        userRepository: FirebaseUserRepository
    ): PostService {
        return FirebasePostService(authenticationRepository, postRepository, userRepository)
    }
}

@Module
internal class FirebaseRepositoryModule {
    @Provides
    @CoreScope
    internal fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @CoreScope
    internal fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }
    }

    @Provides
    @CoreScope
    internal fun provideAuthenticationRepository(
        firebaseAuth: FirebaseAuth
    ): FirebaseAuthenticationRepository {
        return FirebaseAuthenticationRepository(firebaseAuth)
    }

    @Provides
    @CoreScope
    internal fun providePostRepository(
        firebaseDatabase: FirebaseDatabase,
        schedulerManager: SchedulerManager
    ): FirebasePostRepository {
        return FirebasePostRepository(firebaseDatabase, schedulerManager)
    }

    @Provides
    @CoreScope
    internal fun provideUserRepository(
        firebaseDatabase: FirebaseDatabase,
        schedulerManager: SchedulerManager
    ): FirebaseUserRepository {
        return FirebaseUserRepository(firebaseDatabase, schedulerManager)
    }
}