package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post

object NewPostResultContract : ActivityResultContract<Post, Post?>() {

    private const val EXTRA_ID = "extra_id"
    private const val EXTRA_AUTHOR = "extra_author"
    private const val EXTRA_CONTENT = "extra_content"
    private const val EXTRA_PUBLISHED = "extra_published"
    private const val EXTRA_LIKES = "extra_likes"
    private const val EXTRA_LIKED_BY_ME = "extra_liked_by_me"
    private const val EXTRA_SHARES = "extra_shares"
    private const val EXTRA_VIDEO = "extra_video" // <-- новое

    override fun createIntent(context: Context, input: Post): Intent =
        Intent(context, NewPostActivity::class.java).apply {
            putExtra(EXTRA_ID, input.id)
            putExtra(EXTRA_AUTHOR, input.author)
            putExtra(EXTRA_CONTENT, input.content)
            putExtra(EXTRA_PUBLISHED, input.published)
            putExtra(EXTRA_LIKES, input.likes)
            putExtra(EXTRA_LIKED_BY_ME, input.likedByMe)
            putExtra(EXTRA_SHARES, input.shares)
            putExtra(EXTRA_VIDEO, input.video) // <-- передаём
        }

    override fun parseResult(resultCode: Int, intent: Intent?): Post? =
        if (resultCode == Activity.RESULT_OK && intent != null) {
            Post(
                id = intent.getLongExtra(EXTRA_ID, 0L),
                author = intent.getStringExtra(EXTRA_AUTHOR) ?: "",
                content = intent.getStringExtra(EXTRA_CONTENT) ?: "",
                published = intent.getStringExtra(EXTRA_PUBLISHED) ?: "",
                likes = intent.getIntExtra(EXTRA_LIKES, 0),
                likedByMe = intent.getBooleanExtra(EXTRA_LIKED_BY_ME, false),
                shares = intent.getIntExtra(EXTRA_SHARES, 0),
                video = intent.getStringExtra(EXTRA_VIDEO) // <-- читаем
            )
        } else {
            null
        }
}


