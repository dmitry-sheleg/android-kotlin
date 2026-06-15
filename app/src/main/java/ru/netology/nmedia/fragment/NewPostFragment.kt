package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        arguments?.textArg?.let(binding.edit::setText)

        AndroidUtils.showKeyboard(binding.edit)

        binding.ok.setOnClickListener {
            val newContent = binding.edit.text.toString().trim()
            if (!newContent.isEmpty()) {
                val newPost = Post(
                    id = 0L,
                    author = "Me",                 // или брать откуда-то из Auth/ViewModel
                    content = newContent,
                    published = "now",              // или использовать DateTime
                    likes = 0,
                    likedByMe = false,
                    shares = 0
                    // video нет в вашем Post, поэтому не добавляем
                )
                viewModel.save(newPost)
                findNavController().navigateUp()
            }
        }

        return binding.root
    }
}