package com.shinwan2.postmaker.core

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shinwan2.postmaker.core.model.FirebaseUserModel
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.model.EditUserProfileRequest
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class FirebaseUserService(
    private val authenticationService: AuthenticationService,
    private val firebaseDatabase: FirebaseDatabase
) : UserService {
    override fun getUser(userId: String?): Single<User> {
        return Single.create<User> { emitter ->
            val nonNullUserId: String? = userId ?: authenticationService.userId
            if (nonNullUserId == null) {
                emitter.onError(IllegalStateException("userId is null"))
                return@create
            }

            firebaseDatabase.getReference("users/$nonNullUserId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Timber.w("getUser $nonNullUserId fails: $error")
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val firebaseUser = snapshot.getValue(FirebaseUserModel::class.java)!!
                            emitter.onSuccess(firebaseUser.toUser(nonNullUserId))
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    }
                })
        }
    }

    override fun editUserProfile(request: EditUserProfileRequest): Completable {
        return Completable.create { emitter ->
            val nonNullUserId: String? = authenticationService.userId
            if (nonNullUserId == null) {
                emitter.onError(IllegalStateException("userId is null"))
                return@create
            }

            val requestMap = request.toMap()
            firebaseDatabase.getReference("users/$nonNullUserId")
                .updateChildren(requestMap)
                .addOnSuccessListener {
                    Timber.d("editUserProfile success")
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    Timber.w(it, "editUserProfile for ID $nonNullUserId $requestMap")
                    emitter.onError(it)
                }
        }
    }

    private fun EditUserProfileRequest.toMap(): Map<String, Any> {
        return mapOf(
            "displayName" to displayName,
            "aboutMe" to aboutMe,
            "photoUrl" to photoUrl
        )
    }

    private fun FirebaseUserModel.toUser(userId: String): User {
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