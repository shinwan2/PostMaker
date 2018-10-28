package com.shinwan2.postmaker.core.firebase.service

import com.shinwan2.postmaker.core.firebase.repository.FirebaseAuthenticationRepository
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import io.reactivex.Completable

class FirebaseAuthenticationService internal constructor(
    private val authenticationRepository: FirebaseAuthenticationRepository
) : AuthenticationService {

    override val userId: String?
        get() = authenticationRepository.userId

    override fun signIn(email: String, password: String): Completable {
        return authenticationRepository.signIn(email, password)
    }

    override fun signUp(email: String, password: String): Completable {
        return authenticationRepository.signUp(email, password)
    }

    override fun signOut(): Completable {
        return authenticationRepository.signOut()
    }

    override fun isSignedIn(userId: String?): Boolean {
        return authenticationRepository.isSignedIn(userId)
    }
}