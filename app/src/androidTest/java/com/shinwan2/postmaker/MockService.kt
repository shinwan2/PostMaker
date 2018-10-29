package com.shinwan2.postmaker

import com.shinwan2.postmaker.domain.PostService
import com.shinwan2.postmaker.domain.UserService
import com.shinwan2.postmaker.domain.model.CreatePostRequest
import com.shinwan2.postmaker.domain.model.CursorList
import com.shinwan2.postmaker.domain.model.EditUserProfileRequest
import com.shinwan2.postmaker.domain.model.Post
import com.shinwan2.postmaker.domain.model.User
import io.reactivex.Completable
import io.reactivex.Single

internal val USER_SERVICE_ALL_OK = object: UserService {
    override fun getUser(userId: String?): Single<User> {
        return Single.just(MOCK_USER_23)
    }

    override fun editUserProfile(request: EditUserProfileRequest): Completable {
        return Completable.complete()
    }
}

internal val POST_SERVICE_ALL_OK = object: PostService {
    override fun createPost(createPostRequest: CreatePostRequest): Completable {
        return Completable.complete()
    }

    override fun getTimelinePosts(cursor: String?, limit: Int): Single<CursorList<Post>> {
        return Single.defer {
            if (cursor == null) {
                Single.just(
                    CursorList(
                        list = listOf(MOCK_POST1),
                        nextCursor = NEXT_CURSOR
                    )
                )
            } else {
                Single.just(
                    CursorList(
                        list = listOf(MOCK_POST2),
                        nextCursor = ""
                    )
                )
            }
        }
    }

    override fun deletePost(postId: String): Completable {
        return Completable.complete()
    }
}

internal val NEXT_CURSOR = "12301i2309"

internal val MOCK_USER_23 = User(
    userId = "23",
    email = "abcdef@email.com",
    displayName = "abcdef",
    joinTimestamp = 123456,
    aboutMe = "",
    photoUrl = ""
)

internal val MOCK_USER_24 = User(
    userId = "24",
    email = "zzzaaa@email.com",
    displayName = "zzzaaa",
    joinTimestamp = 123456,
    aboutMe = "",
    photoUrl = ""
)

internal val MOCK_POST1 = Post(
    postId = "1",
    userId = "23",
    user = MOCK_USER_23,
    textContent = "Some post",
    createdTimestamp = 130123938
)

internal val MOCK_POST2 = Post(
    postId = "2",
    userId = "24",
    user = MOCK_USER_24,
    textContent = "Other post",
    createdTimestamp = 130123938
)