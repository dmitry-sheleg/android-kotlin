package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl


private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likes = 0,
    likedByMe = false,
    shares = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryRoomImpl(
        AppDb.getInstance(application).postDao
    )
    val data = repository.getAll()

    val edited = MutableLiveData(empty)

    fun save(post: Post) {
        repository.save(post)
        edited.value = null
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}