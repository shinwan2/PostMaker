package com.shinwan2.postmaker.core

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.shinwan2.postmaker.domain.auth.AuthenticationException
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import io.reactivex.Completable
import java.util.concurrent.ExecutionException

class FirebaseAuthenticationService(
    private val firebaseAuth: FirebaseAuth
) : AuthenticationService {
    override fun signIn(email: String, password: String): Completable {
        return Completable.create { emitter ->
            try {
                Tasks.await(firebaseAuth.signInWithEmailAndPassword(email, password))
                emitter.onComplete()
            } catch (e: ExecutionException) {
                emitter.onError(AuthenticationException(e.cause!!))
            }
        }
    }

    override fun signUp(email: String, password: String): Completable {
        return Completable.create { emitter ->
            try {
                Tasks.await(firebaseAuth.createUserWithEmailAndPassword(email, password))
                emitter.onComplete()
            } catch (e: ExecutionException) {
                emitter.onError(AuthenticationException(e.cause!!))
            }
        }
    }

    override fun signOut(): Completable {
        return Completable.create { emitter ->
            try {
                firebaseAuth.signOut()
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(AuthenticationException(e))
            }
        }
    }

    override fun isSignedIn(userId: String?): Boolean {
        if (userId == null) return firebaseAuth.currentUser != null
        return firebaseAuth.currentUser?.uid == userId
    }
}