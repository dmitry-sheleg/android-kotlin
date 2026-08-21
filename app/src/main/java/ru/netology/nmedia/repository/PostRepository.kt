package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long): Post
    fun unlikeById(id: Long): Post
    fun save(post: Post)
    fun removeById(id: Long)

    fun getAllAsync(callback: GetAllCallback)
    fun saveAsync(post: Post, callback: SaveCallback)
    fun likeByIdAsync(id: Long, callback: LikeCallback)
    fun unlikeByIdAsync(id: Long, callback: LikeCallback)
    fun removeByIdAsync(id: Long, callback: RemoveCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(code: Int?) {}
    }

    interface SaveCallback {
        fun onSuccess()
        fun onError(code: Int?)
    }

    interface LikeCallback {
        fun onSuccess(post: Post)
        fun onError(code: Int?)
    }

    interface RemoveCallback {
        fun onSuccess()
        fun onError(code: Int?)
    }
}
