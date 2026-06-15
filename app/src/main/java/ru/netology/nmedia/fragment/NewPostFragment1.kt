package ru.netology.nmedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.getValue

class NewPostFragment1 : Fragment() {

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_AUTHOR = "extra_author"
        const val EXTRA_CONTENT = "extra_content"
        const val EXTRA_PUBLISHED = "extra_published"
        const val EXTRA_LIKES = "extra_likes"
        const val EXTRA_LIKED_BY_ME = "extra_liked_by_me"
        const val EXTRA_SHARES = "extra_shares"
        const val EXTRA_VIDEO = "extra_video"

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

        val intent = Intent()
        val id = intent.getLongExtra(EXTRA_ID, 0L)
        val author = intent.getStringExtra(EXTRA_AUTHOR) ?: ""
        val content = intent.getStringExtra(EXTRA_CONTENT) ?: ""
        val published = intent.getStringExtra(EXTRA_PUBLISHED) ?: ""
        val likes = intent.getIntExtra(EXTRA_LIKES, 0)
        val likedByMe = intent.getBooleanExtra(EXTRA_LIKED_BY_ME, false)
        val shares = intent.getIntExtra(EXTRA_SHARES, 0)
        // Получаем ссылку на видео (может быть пустой)
        val video = intent.getStringExtra(EXTRA_VIDEO) ?: ""

        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
//        binding.edit.setText(content)
        arguments?.textArg?.let(binding.edit::setText)

        // Заполняем поле для ссылки на видео при редактировании
        binding.editVideo.setText(video)
        AndroidUtils.showKeyboard(binding.edit)

        binding.ok.setOnClickListener {
            val newContent = binding.edit.text.toString().trim()
            if (!newContent.isEmpty()) {
//                setResult(RESULT_CANCELED)
//                finish()
                return@setOnClickListener
            }

            // Берём обновлённую ссылку на видео из поля ввода
            val videoLink = binding.editVideo.text.toString().trim().takeIf { it.isNotBlank() }

            // ВАЛИДАЦИЯ: только rutube.ru (и http/https)
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

            val resultIntent = Intent().apply {
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_AUTHOR, author)
                putExtra(EXTRA_CONTENT, newContent)
                putExtra(EXTRA_PUBLISHED, published)
                putExtra(EXTRA_LIKES, likes)
                putExtra(EXTRA_LIKED_BY_ME, likedByMe)
                putExtra(EXTRA_SHARES, shares)
                // Передаём либо новую ссылку, либо null, если поле пустое
                putExtra(EXTRA_VIDEO, videoLink)
            }
//            setResult(RESULT_OK, resultIntent)
//            finish()
//            viewModel.save(resultIntent)
        }

        return binding.root
    }
}