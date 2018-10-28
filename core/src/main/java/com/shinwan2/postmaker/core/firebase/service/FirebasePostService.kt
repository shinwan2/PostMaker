package com.shinwan2.postmaker.core.firebase.service

import com.shinwan2.postmaker.core.firebase.repository.FirebaseAuthenticationRepository
import com.shinwan2.postmaker.core.firebase.repository.FirebasePostRepository
import com.shinwan2.postmaker.core.firebase.repository.FirebaseUserRepository
import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.model.CreatePostRequest
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.Post
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

private const val MINIMAL_POSTS = 10
private const val MAXIMAL_POSTS = 20

class FirebasePostService internal constructor(
    private val authenticationRepository: FirebaseAuthenticationRepository,
    private val postRepository: FirebasePostRepository,
    private val userRepository: FirebaseUserRepository
) : PostService {
    override fun createPost(createPostRequest: CreatePostRequest): Completable {
        return Completable.defer {
            val nonNullUserId: String = authenticationRepository.userId
                ?: return@defer Completable.error(IllegalStateException("userId is null"))
            postRepository.createPost(nonNullUserId, createPostRequest)
        }.doOnError { Timber.w(it, "createPost $createPostRequest fails: ${it.message}") }
    }

    override fun getTimelinePosts(cursor: String?, limit: Int): Single<CursorList<Post>> {
        return Single.defer {
            postRepository.getTimelinePosts(cursor, limit.coerceIn(MINIMAL_POSTS, MAXIMAL_POSTS))
                .firstOrError()
                .map { cursoredPosts ->
                    val getAllUsersForPosts = cursoredPosts.list
                        .asSequence()
                        .map { post -> post.userId }
                        .toSet()
                        .asSequence()
                        .map { userId -> userRepository.getUser(userId).firstOrError() }
                        .toList()
                    if (getAllUsersForPosts.isEmpty()) {
                        return@map cursoredPosts
                    }
                    Single.zip(getAllUsersForPosts) { users -> users.toList() }
                        .map { usersUnsafe ->
                            @Suppress("UNCHECKED_CAST")
                            val users = (usersUnsafe as List<User>).associateBy { it.userId }
                            CursorList(
                                list = cursoredPosts.list.map { it.copy(user = users[it.userId]) },
                                nextCursor = cursoredPosts.nextCursor
                            )
                        }
                        .blockingGet()
                }
        }.doOnError { Timber.w(it, "getTimelinePosts ($cursor,$limit) fails: ${it.message}") }
    }

    override fun deletePost(postId: String): Completable {
        return Completable.defer {
            val nonNullUserId: String = authenticationRepository.userId
                ?: return@defer Completable.error(IllegalStateException("userId is null"))
            postRepository.deletePost(nonNullUserId, postId)
        }.doOnError { Timber.w(it, "deletePost with ID:$postId fails: ${it.message}") }
    }
}