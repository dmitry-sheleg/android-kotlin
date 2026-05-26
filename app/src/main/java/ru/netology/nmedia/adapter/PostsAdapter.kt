package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

typealias OnItemListener = (post: Post) -> Unit

class PostsAdapter(
    private val onLikeListener: OnItemListener,
    private val onShareListener: OnItemListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener)
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnItemListener,
    private val onShareListener: OnItemListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeCount.text = numToShortString(post.likes)
            like.setImageResource(
                if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24
            )
            shareCount.text = numToShortString(post.shares)

            like.setOnClickListener{
                onLikeListener(post)
            }

            share.setOnClickListener {
                onShareListener(post)
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


object PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

}

