package com.shinwan2.postmaker.core

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import io.reactivex.Completable

class FirebaseAuthenticationService(
    private val firebaseAuth: FirebaseAuth
) : AuthenticationService {
    override fun signIn(email: String, password: String): Completable {
        return Completable.create { emitter ->
            try {
                Tasks.await(firebaseAuth.signInWithEmailAndPassword(email, password))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun signUp(email: String, password: String): Completable {
        return Completable.create { emitter ->
            try {
                Tasks.await(firebaseAuth.createUserWithEmailAndPassword(email, password))
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    override fun signOut(): Completable {
        return Completable.create { emitter ->
            firebaseAuth.signOut()
            emitter.onComplete()
        }
    }
}