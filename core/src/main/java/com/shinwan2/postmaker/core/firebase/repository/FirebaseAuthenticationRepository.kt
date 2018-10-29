package com.shinwan2.postmaker.core.firebase.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.shinwan2.postmaker.domain.auth.AuthenticationException
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.concurrent.ExecutionException

internal class FirebaseAuthenticationRepository(private val firebaseAuth: FirebaseAuth) {

    val userId: String?
        get() = firebaseAuth.uid

    fun signIn(email: String, password: String): Completable {
        return Completable.create { emitter ->
            try {
                Tasks.await(firebaseAuth.signInWithEmailAndPassword(email, password))
                emitter.onComplete()
            } catch (e: ExecutionException) {
                emitter.onError(AuthenticationException(e.cause!!))
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun signUp(email: String, password: String): Completable {
        return Completable.create { emitter ->
            try {
                Tasks.await(firebaseAuth.createUserWithEmailAndPassword(email, password))
                emitter.onComplete()
            } catch (e: ExecutionException) {
                emitter.onError(AuthenticationException(e.cause!!))
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun signOut(): Completable {
        return Completable.create { emitter ->
            try {
                firebaseAuth.signOut()
                emitter.onComplete()
            } catch (e: Throwable) {
                emitter.onError(AuthenticationException(e))
            }
        }
    }

    fun isSignedIn(userId: String?): Boolean {
        if (userId == null) return firebaseAuth.currentUser != null
        return firebaseAuth.currentUser?.uid == userId
    }

    fun authStateChanged(): Observable<Boolean> {
        return Observable.create<Boolean> { emitter ->
            val authStateListener = FirebaseAuth.AuthStateListener {
                if (emitter.isDisposed) return@AuthStateListener
                emitter.onNext(it.currentUser != null)
            }
            firebaseAuth.addAuthStateListener(authStateListener)
            emitter.setCancellable {
                firebaseAuth.removeAuthStateListener(authStateListener)
            }
        }
    }
}