package com.shinwan2.postmaker.domain.model

data class CursorList<T>(
    val list: List<T>,
    val nextCursor: String
)