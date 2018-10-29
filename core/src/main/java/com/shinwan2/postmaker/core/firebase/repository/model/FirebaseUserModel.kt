package com.shinwan2.postmaker.core.firebase.repository.model

internal data class FirebaseUserModel(
    var email: String? = null,
    var displayName: String? = null,
    var photoUrl: String? = null,
    var aboutMe: String? = null,
    var joinTimestamp: Long? = null
)