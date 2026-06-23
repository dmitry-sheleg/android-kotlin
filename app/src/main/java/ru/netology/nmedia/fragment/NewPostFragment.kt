package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        viewModel.edited.value?.let { existingPost ->
            binding.edit.setText(existingPost.content)
            binding.editVideo.setText(existingPost.video ?: "")
        } ?: run {
            arguments?.textArg?.let(binding.edit::setText)
        }

        AndroidUtils.showKeyboard(binding.edit)

        binding.ok.setOnClickListener {
            val newContent = binding.edit.text.toString().trim()
            if (newContent.isEmpty()) {
                binding.edit.error = "Пост не может быть пустым"
                return@setOnClickListener
            }

            val videoLink = binding.editVideo.text.toString().trim().takeIf { it.isNotBlank() }

            if (videoLink != null) {
                val uri = videoLink.toUri()
                val host = uri.host ?: ""
                if (!host.endsWith("rutube.ru")) {
                    binding.editVideo.error = "Ссылка должна быть с rutube.ru"
                    return@setOnClickListener
                } else {
                    binding.editVideo.error = null
                }
            }
            val post = if (viewModel.edited.value != null) {
                viewModel.edited.value!!.copy(content = newContent, video = videoLink)
            } else {
                Post(
                    id = 0L,
                    author = "Me",
                    content = newContent,
                    published = "now",
                    likes = 0,
                    likedByMe = false,
                    shares = 0,
                    video = videoLink
                )
            }
            viewModel.save(post)
            findNavController().navigateUp()
        }

        return binding.root
    }
}