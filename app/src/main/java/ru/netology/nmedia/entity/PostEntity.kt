package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "published")
    val published: String,
    @ColumnInfo(name = "likes")
    val likes: Int = 0,
    @ColumnInfo(name = "likedByMe")
    val likedByMe: Boolean,
    @ColumnInfo(name = "shares")
    val shares: Int = 0,
    @ColumnInfo(name = "video")
    val video: String? = null
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likes = likes,
        likedByMe = likedByMe,
        shares = shares,
        video = video
    )

    companion object {
        fun fromDto(dto: Post): PostEntity = with(dto) {
            PostEntity(
            id = id,
            author = author,
            content = content,
            published = published,
            likes = likes,
            likedByMe = likedByMe,
            shares = shares,
            video = video
            )
        }
    }
}

