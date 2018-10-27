package com.shinwan2.postmaker.domain.model

data class EditUserProfileRequest(
    var displayName: String,
    var aboutMe: String,
    var photoUrl: String
)