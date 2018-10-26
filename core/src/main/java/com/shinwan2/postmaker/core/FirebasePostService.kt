package com.shinwan2.postmaker.core

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.shinwan2.postmaker.core.model.FirebasePostModel
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.domain.model.CreatePostRequest
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class FirebasePostService(
    private val authenticationService: AuthenticationService,
    private val firebaseDatabase: FirebaseDatabase
) : PostService {
    override fun createPost(createPostRequest: CreatePostRequest): Completable {
        return Completable.create { emitter ->
            val nonNullUserId: String? = authenticationService.userId
            if (nonNullUserId == null) {
                emitter.onError(IllegalStateException("userId is null"))
                return@create
            }
            val postId = firebaseDatabase.getReference("posts").push().key
            val postValues = mapOf(
                "userId" to nonNullUserId,
                "textContent" to createPostRequest.textContext,
                "createdTimestamp" to ServerValue.TIMESTAMP
            )
            val childUpdates = mutableMapOf<String, Any>()
            childUpdates["/posts/$postId"] = postValues
            childUpdates["/user-posts/$nonNullUserId/$postId"] = true

            firebaseDatabase.reference.updateChildren(childUpdates)
                .addOnSuccessListener {
                    Timber.d("createPost succeeds postId: $postId")
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    Timber.w(it, "createPost fails")
                    emitter.onError(it)
                }
        }
    }

    override fun getTimelinePosts(cursor: String?, limit: Int): Single<CursorList<Post>> {
        return Single.create { emitter ->
            var query: Query = firebaseDatabase.getReference("posts")
            if (!cursor.isNullOrBlank()) {
                query = query.startAt(cursor)
            }
            query.limitToFirst(limit)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        val exception = error.toException()
                        Timber.w(exception, "getTimelinePosts fails")
                        emitter.onError(exception)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val posts = snapshot.children
                                .map { it.key to it.getValue(FirebasePostModel::class.java)!! }
                                .map { (postId, post) ->
                                    Post(
                                        postId = checkNotNull(postId),
                                        userId = checkNotNull(post.userId),
                                        textContent = checkNotNull(post.textContent),
                                        createdTimestamp = checkNotNull(post.createdTimestamp)
                                    )
                                }
                                .toList()
                            val nextCursor = posts.lastOrNull()?.postId.orEmpty()
                            emitter.onSuccess(CursorList(posts, nextCursor))
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    }
                })
        }
    }

    override fun deletePost(postId: String): Completable {
        return Completable.create { emitter ->
            val nonNullUserId: String? = authenticationService.userId
            if (nonNullUserId == null) {
                emitter.onError(IllegalStateException("userId is null"))
                return@create
            }
            val childUpdates = mutableMapOf<String, Any?>()
            childUpdates["/posts/$postId"] = null
            childUpdates["/user-posts/$nonNullUserId/$postId"] = null

            firebaseDatabase.reference.updateChildren(childUpdates)
                .addOnSuccessListener {
                    Timber.d("deletePost succeeds postId: $postId")
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    Timber.w(it, "deletePost fails")
                    emitter.onError(it)
                }
        }
    }
}