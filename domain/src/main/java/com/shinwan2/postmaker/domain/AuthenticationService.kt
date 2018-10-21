package com.shinwan2.postmaker.domain

import io.reactivex.Completable

interface AuthenticationService {
    fun signIn(email: String, password: String): Completable
    fun signUp(email: String, password: String): Completable
    fun signOut(): Completable
}
