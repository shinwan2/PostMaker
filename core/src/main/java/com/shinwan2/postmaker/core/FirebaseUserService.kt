package com.shinwan2.postmaker.core

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shinwan2.postmaker.core.model.FirebaseUserModel
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Single
import timber.log.Timber

class FirebaseUserService(
    private val authenticationService: AuthenticationService,
    private val firebaseDatabase: FirebaseDatabase
) : UserService {
    override fun getUser(userId: String?): Single<User> {
        return Single.create<User> { emitter ->
            val nonNullUserId: String = userId ?: authenticationService.userId!!
            firebaseDatabase.getReference("users/$nonNullUserId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Timber.w("getUser $nonNullUserId fails: $error")
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val firebaseUser = snapshot.getValue(FirebaseUserModel::class.java)!!
                            emitter.onSuccess(firebaseUser.map(nonNullUserId))
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    }
                })
        }
    }

    private fun FirebaseUserModel.map(userId: String): User {
        return User(
            userId = userId,
            email = checkNotNull(email),
            displayName = checkNotNull(displayName),
            aboutMe = checkNotNull(aboutMe),
            joinTimestamp = checkNotNull(joinTimestamp),
            photoUrl = checkNotNull(photoUrl)
        )
    }
}