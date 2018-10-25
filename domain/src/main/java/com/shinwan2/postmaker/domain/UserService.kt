package com.shinwan2.postmaker.domain

import com.shinwan2.postmaker.domain.model.EditUserProfileRequest
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface UserService {
    fun getUser(userId: String? = null): Single<User>
    fun editUserProfile(request: EditUserProfileRequest): Completable
}