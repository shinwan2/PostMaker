package com.shinwan2.postmaker.core.firebase.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.shinwan2.postmaker.core.firebase.repository.model.FirebasePostModel
import com.shinwan2.postmaker.domain.SchedulerManager
import com.shinwan2.postmaker.domain.model.CreatePostRequest
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import io.reactivex.Completable
import io.reactivex.Observable

internal class FirebasePostRepository(
    private val firebaseDatabase: FirebaseDatabase,
    private val schedulerManager: SchedulerManager
) {
    fun createPost(userId: String, createPostRequest: CreatePostRequest): Completable {
        return Completable.create { emitter ->
            val postId = firebaseDatabase.getReference("posts").push().key
            val postValues = mapOf(
                "userId" to userId,
                "textContent" to createPostRequest.textContext,
                "createdTimestamp" to ServerValue.TIMESTAMP
            )
            val childUpdates = mutableMapOf<String, Any>()
            childUpdates["/posts/$postId"] = postValues
            childUpdates["/user-posts/$userId/$postId"] = true

            firebaseDatabase.reference.updateChildren(childUpdates)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
            .observeOn(schedulerManager.backgroundThreadScheduler)
    }

    fun getTimelinePosts(cursor: String?, limit: Int): Observable<CursorList<Post>> {
        return Observable.create<CursorList<Post>> { emitter ->
            var query: Query = firebaseDatabase.getReference("posts").orderByKey()
            var limitIncludingBoundary = limit
            if (!cursor.isNullOrBlank()) {
                query = query.endAt(cursor)
                limitIncludingBoundary += 1
            }
            query = query.limitToLast(limitIncludingBoundary)
            val eventListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    if (emitter.isDisposed) return
                    emitter.onError(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (emitter.isDisposed) return
                    try {
                        val posts = snapshot.children
                            .map {
                                val postId = checkNotNull(it.key)
                                val post = checkNotNull(
                                    it.getValue(FirebasePostModel::class.java)
                                )
                                postId to post
                            }
                            .filterNot { it.first == cursor }
                            .map { (postId, post) ->
                                Post(
                                    postId = postId,
                                    userId = checkNotNull(post.userId),
                                    textContent = checkNotNull(post.textContent),
                                    createdTimestamp = checkNotNull(post.createdTimestamp)
                                )
                            }
                            .take(limit)
                            .sortedByDescending { it.createdTimestamp }
                        val nextCursor = posts.getOrNull(limit - 1)?.postId.orEmpty()
                        emitter.onNext(CursorList(posts, nextCursor))
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                }
            }
            query.addValueEventListener(eventListener)
            emitter.setCancellable { query.removeEventListener(eventListener) }
        }
            .observeOn(schedulerManager.backgroundThreadScheduler)
    }

    fun deletePost(userId: String, postId: String): Completable {
        return Completable.create { emitter ->
            val childUpdates = mutableMapOf<String, Any?>()
            childUpdates["/posts/$postId"] = null
            childUpdates["/user-posts/$userId/$postId"] = null

            firebaseDatabase.reference.updateChildren(childUpdates)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.onError(it) }
        }
            .observeOn(schedulerManager.backgroundThreadScheduler)
    }
}