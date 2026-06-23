package ru.netology.nmedia.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostDetailBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.MyUtils.numToShortString
import ru.netology.nmedia.viewmodel.PostViewModel

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPost: Post

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)

        val postId = arguments?.getLong("postId")

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val updatedPost = posts.find { it.id == postId }
            if (updatedPost != null) {
                currentPost = updatedPost
                bindPost(currentPost)
            }
        }

        viewModel.data.value?.find { it.id == postId }?.let { post ->
            currentPost = post
            bindPost(post)
        }

        binding.postContentInclude.apply {
            if (!currentPost.video.isNullOrBlank()) {
                videoBlock.setOnClickListener {
                    openVideo(
                        context = binding.root.context,
                        videoUrl = currentPost.video!!
                    )
                }
            } else {
                videoBlock.setOnClickListener(null)
            }

            menu.setOnClickListener { view ->
                PopupMenu(requireContext(), view).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                viewModel.edit(currentPost)
                                findNavController().navigate(R.id.action_postDetailFragment_to_newPostFragment)
                                true
                            }

                            R.id.remove -> {
                                viewModel.removeById(currentPost.id)
                                findNavController().navigateUp()
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                viewModel.likeById(currentPost.id)
            }

            share.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, currentPost.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.shareById(currentPost.id)
            }

        }
        return binding.root
    }

    private fun bindPost(post: Post) {
        binding.postContentInclude.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = numToShortString(post.likes)
            share.text = numToShortString(post.shares)
            videoBlock.visibility = if (!post.video.isNullOrBlank()) View.VISIBLE else View.GONE
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


