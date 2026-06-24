package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private companion object {
        const val ACTION_KEY = "action"
        const val CONTENT_KEY = "content"
        const val CHANNEL_ID = "remote"
        val gson = Gson()
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val actionString = message.data[ACTION_KEY] ?: return
        val action = try {
            Action.valueOf(actionString)
        } catch (e: IllegalArgumentException) {
            null
        }

        when (action) {
            Action.LIKE -> handleLike(
                gson.fromJson(
                    message.data[CONTENT_KEY],
                    Like::class.java
                )
            )

            Action.NEW_POST -> handleNewPost(
                gson.fromJson(
                    message.data[CONTENT_KEY],
                    NewPost::class.java
                )
            )

            null -> {
                Log.w(
                    "FCMService",
                    "Получено неизвестное уведомление: $actionString. Data: ${message.data}"
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        println(token)
    }

    private fun handleLike(content: Like) {
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    getString(
                        R.string.notification_user_liked,
                        content.userName,
                        content.postAuthor,
                    )
                ).setPriority(NotificationCompat.PRIORITY_DEFAULT).build()

        notify(notification)
    }

    private fun handleNewPost(content: NewPost) {
        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(
                    getString(
                        R.string.notification_new_post, content.postAuthor
                    )
                )
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(content.postContent)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        notify(notification)
    }

    private fun notify(notification: Notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(Random.nextInt(100_000), notification)
        }
    }

}

enum class Action {
    LIKE, NEW_POST,
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NewPost(
    val postId: Long,
    val postAuthor: String,
    val postContent: String,
)