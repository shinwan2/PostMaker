package com.shinwan2.postmaker.domain.model

data class User(
    var userId: String,
    var displayName: String,
    var joinTime: Long,
    var lastLoginTime: Long,
    var profilePictureUrl: String
)