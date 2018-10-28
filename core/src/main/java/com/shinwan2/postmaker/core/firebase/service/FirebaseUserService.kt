package com.shinwan2.postmaker.core.firebase.service

import com.shinwan2.postmaker.core.firebase.repository.FirebaseAuthenticationRepository
import com.shinwan2.postmaker.core.firebase.repository.FirebaseUserRepository
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.model.EditUserProfileRequest
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class FirebaseUserService internal constructor(
    private val authenticationRepository: FirebaseAuthenticationRepository,
    private val userRepository: FirebaseUserRepository
) : UserService {
    override fun getUser(userId: String?): Single<User> {
        return Single.defer {
            val nonNullUserId: String = authenticationRepository.userId
                ?: return@defer Single.error<User>(IllegalStateException("userId is null"))
            userRepository.getUser(nonNullUserId)
        }.doOnError { Timber.w(it, "getUser with ID:$userId fails: ${it.message}") }
    }

    override fun editUserProfile(request: EditUserProfileRequest): Completable {
        return Completable.defer {
            val nonNullUserId: String = authenticationRepository.userId
                ?: return@defer Completable.error(IllegalStateException("userId is null"))
            userRepository.editUserProfile(nonNullUserId, request)
        }.doOnError { Timber.w(it, "editUserProfile $request fails: ${it.message}") }
    }
}