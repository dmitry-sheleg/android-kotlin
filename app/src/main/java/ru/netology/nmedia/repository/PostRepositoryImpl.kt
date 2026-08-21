package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val logging = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .build()

    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = BuildConfig.BASE_URL
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()
        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        return callback.onError(response.code)
                    }

                    val body = response.body?.string() ?: return callback.onError(response.code)

                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(null)
                    } finally {
                        response.close()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(null)
                }

            })
    }

    override fun likeById(id: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id/likes")
            .post(RequestBody.EMPTY)
            .build()

        val response = client.newCall(request).execute()
        val responseText = response.body.string()

        return gson.fromJson(responseText, Post::class.java)
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .post(RequestBody.EMPTY)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    return callback.onError(response.code)
                }

                val body = response.body?.string() ?: return callback.onError(response.code)

                try {
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(null)
                } finally {
                    response.close()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(null)
            }
        })
    }


    override fun unlikeById(id: Long): Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts/$id/likes")
            .delete(RequestBody.EMPTY)
            .build()

        val response = client.newCall(request).execute()
        val responseText = response.body.string()

        return gson.fromJson(responseText, Post::class.java)
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    return callback.onError(response.code)
                }

                val body = response.body?.string() ?: return callback.onError(response.code)

                try {
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(null)
                } finally {
                    response.close()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(null)
            }
        })
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun saveAsync(post: Post, callback: PostRepository.SaveCallback) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    callback.onSuccess()
                } catch (e: Exception) {
                    callback.onError(null)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(null)
            }
        })
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemoveCallback) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onResponse(call: Call, response: Response) {
                try {
                    println("52 ${Thread.currentThread().name}")
                    if (!response.isSuccessful) {
                        return callback.onError(response.code)
                    }
                    callback.onSuccess()
                } catch (e: Exception) {
                    callback.onError(null)
                } finally {
                    response.close()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(null)
            }
        })
    }
}
