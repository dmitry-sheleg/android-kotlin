package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.util.AndroidUtils

class NewPostActivity : AppCompatActivity() {

    private companion object {
        private const val EXTRA_ID = "extra_id"
        private const val EXTRA_AUTHOR = "extra_author"
        private const val EXTRA_CONTENT = "extra_content"
        private const val EXTRA_PUBLISHED = "extra_published"
        private const val EXTRA_LIKES = "extra_likes"
        private const val EXTRA_LIKED_BY_ME = "extra_liked_by_me"
        private const val EXTRA_SHARES = "extra_shares"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        val id = intent.getLongExtra(EXTRA_ID, 0L)
        val author = intent.getStringExtra(EXTRA_AUTHOR) ?: ""
        val content = intent.getStringExtra(EXTRA_CONTENT) ?: ""
        val published = intent.getStringExtra(EXTRA_PUBLISHED) ?: ""
        val likes = intent.getIntExtra(EXTRA_LIKES, 0)
        val likedByMe = intent.getBooleanExtra(EXTRA_LIKED_BY_ME, false)
        val shares = intent.getIntExtra(EXTRA_SHARES, 0)

        binding.edit.setText(content)
        AndroidUtils.showKeyboard(binding.edit)

        binding.ok.setOnClickListener {
            val newContent = binding.edit.text.toString().trim()
            if (newContent.isEmpty()) {
                setResult(RESULT_CANCELED)
                finish()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_AUTHOR, author)
                putExtra(EXTRA_CONTENT, newContent)
                putExtra(EXTRA_PUBLISHED, published)
                putExtra(EXTRA_LIKES, likes)
                putExtra(EXTRA_LIKED_BY_ME, likedByMe)
                putExtra(EXTRA_SHARES, shares)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}