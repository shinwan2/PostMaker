package com.shinwan2.postmaker.core.model

data class FirebasePostModel(
    var userId: String? = null,
    val textContent: String? = null,
    val createdTimestamp: Long? = null
)