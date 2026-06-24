package ru.netology.nmedia.adapter

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.MyUtils.numToShortString

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onOpenPost(post: Post)      // открытие отдельного поста
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = numToShortString(post.likes)
            share.text = numToShortString(post.shares)

            // Показываем/скрываем блок с видео в зависимости от наличия ссылки
            videoBlock.visibility = if (post.video != null) View.VISIBLE else View.GONE

            if (post.video != null) {
                videoBlock.setOnClickListener {
                    openVideo(
                        context = binding.root.context,
                        videoUrl = post.video
                    )
                }
            } else {
                videoBlock.setOnClickListener(null)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            binding.root.setOnClickListener {
                onInteractionListener.onOpenPost(post)
            }
        }
    }

    private fun openVideo(context: Context, videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
            .apply { setPackage("ru.rutube.app") }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val browserIntent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
            val chooserIntent = Intent.createChooser(browserIntent, "Открыть с помощью")
            context.startActivity(chooserIntent)
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

