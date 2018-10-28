package com.shinwan2.postmaker.core.firebase.service

import com.shinwan2.postmaker.core.firebase.repository.FirebaseAuthenticationRepository
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import io.reactivex.Completable
import timber.log.Timber

class FirebaseAuthenticationService internal constructor(
    private val authenticationRepository: FirebaseAuthenticationRepository
) : AuthenticationService {

    override val userId: String?
        get() = authenticationRepository.userId

    override fun signIn(email: String, password: String): Completable {
        return authenticationRepository.signIn(email, password)
            .doOnError { Timber.w(it, "signIn fails: ${it.message}") }
    }

    override fun signUp(email: String, password: String): Completable {
        return authenticationRepository.signUp(email, password)
            .doOnError { Timber.w(it, "signUp fails: ${it.message}") }
    }

    override fun signOut(): Completable {
        return authenticationRepository.signOut()
            .doOnError { Timber.w(it, "signOut fails: ${it.message}") }
    }

    override fun isSignedIn(userId: String?): Boolean {
        return authenticationRepository.isSignedIn(userId)
    }
}