package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left + v.paddingLeft, systemBars.top + v.paddingTop, systemBars.right + v.paddingRight, systemBars.bottom + v.paddingBottom)
            insets
        }

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = 999,
            likedByMe = false
        )
        with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            if (post.likedByMe) {
                like.setImageResource(R.drawable.ic_liked_24)
            }
            likeCount.text = numToShortString(post.likes)

            like.setOnClickListener {
                post.likedByMe = !post.likedByMe
                like.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
                )
                if (post.likedByMe) post.likes++ else post.likes--
                likeCount.text = numToShortString(post.likes)
            }
        }
    }

    fun numToShortString(value: Int): String {
        return when {
            value < 10_000 -> {
                val thousands = value / 1_000
                val hundreds = (value % 1_000) / 100

                if (thousands == 0) {
                    value.toString()
                } else if (hundreds == 0) {
                    "${thousands}K"  // Убираем точку и ноль, если сотни = 0
                } else {
                    "${thousands}.${hundreds}K"
                }
            }

            value in 10_000 until 1_000_000 -> {
                "${value / 1_000}K"  // Всегда без дробной части
            }

            else -> {
                val millions = value / 1_000_000
                val hundredThousands = (value % 1_000_000) / 100_000

                if (hundredThousands == 0) {
                    "${millions}M"  // Убираем точку и ноль, если сотни тысяч = 0
                } else {
                    "${millions}.${hundredThousands}M"
                }
            }
        }
    }
}