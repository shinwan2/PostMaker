package com.shinwan2.postmaker.core.firebase.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shinwan2.postmaker.core.firebase.repository.model.FirebaseUserModel
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.model.EditUserProfileRequest
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

internal class FirebaseUserRepository(
    private val firebaseDatabase: FirebaseDatabase,
    private val schedulerManager: SchedulerManager
) {
    fun getUser(userId: String): Single<User> {
        return Single.create<User> { emitter ->
            firebaseDatabase.getReference("users/$userId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(error.toException())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val firebaseUser = snapshot.getValue(FirebaseUserModel::class.java)!!
                            emitter.onSuccess(firebaseUser.toUser(userId))
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    }
                })
        }
            .observeOn(schedulerManager.backgroundThreadScheduler)
    }

    fun editUserProfile(userId: String, request: EditUserProfileRequest): Completable {
        return Completable.create { emitter ->
            val requestMap = request.toMap()
            firebaseDatabase.getReference("users/$userId")
                .updateChildren(requestMap)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
            .observeOn(schedulerManager.backgroundThreadScheduler)
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