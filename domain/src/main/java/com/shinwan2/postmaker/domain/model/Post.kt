package com.shinwan2.postmaker.domain.model

data class Post(
    val postId: String,
    val userId: String,
    val user: User? = null,
    val textContent: String,
    val createdTimestamp: Long
) {
    companion object {
        const val CONTENT_MAX_LENGTH = 200
    }
}