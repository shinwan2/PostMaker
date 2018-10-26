package com.shinwan2.postmaker.domain.model

data class Post(
    val userId: String,
    val user: User?,
    val textContent: String,
    val createdTimestamp: Long
)