package com.shinwan2.postmaker.domain

import com.shinwan2.postmaker.domain.model.CreatePostRequest
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import io.reactivex.Completable
import io.reactivex.Single

interface PostService {
    fun createPost(createPostRequest: CreatePostRequest): Completable
    fun getTimelinePosts(cursor: String? = null, limit: Int): Single<CursorList<Post>>
    fun deletePost(postId: String): Completable
}