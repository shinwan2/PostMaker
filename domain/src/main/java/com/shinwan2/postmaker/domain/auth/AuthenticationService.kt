package com.shinwan2.postmaker.domain.auth

import io.reactivex.Completable

interface AuthenticationService {
    val userId: String?
    fun signIn(email: String, password: String): Completable
    fun signUp(email: String, password: String): Completable
    fun signOut(): Completable
    fun isSignedIn(userId: String? = null): Boolean
}
