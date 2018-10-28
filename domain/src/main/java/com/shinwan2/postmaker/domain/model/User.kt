package com.shinwan2.postmaker.domain.model

data class User(
    var userId: String,
    var email: String,
    var displayName: String,
    var aboutMe: String,
    var joinTimestamp: Long,
    var photoUrl: String,
    var posts: CursorList<Post>? = null
)