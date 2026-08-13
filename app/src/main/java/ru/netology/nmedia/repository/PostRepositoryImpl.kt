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
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
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
        println("11 ${Thread.currentThread().name}")
        client.newCall(request)
            .enqueue(object: Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("Empty body")
                    try {
                        println("12 ${Thread.currentThread().name}")
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })
        println("13 ${Thread.currentThread().name}")
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
        println("21 ${Thread.currentThread().name}")
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    println("22 ${Thread.currentThread().name}")
                    val body = response.body?.string() ?: throw IOException("Empty response")
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(e)
                } finally {
                    response.close()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
        println("23 ${Thread.currentThread().name}")
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

        println("31 ${Thread.currentThread().name}")
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    println("32 ${Thread.currentThread().name}")
                    val body = response.body?.string() ?: throw IOException("Empty response")
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                } catch (e: Exception) {
                    callback.onError(e)
                } finally {
                    response.close()
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
        println("33 ${Thread.currentThread().name}")
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
        println("41 ${Thread.currentThread().name}")
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    println("42 ${Thread.currentThread().name}")
                    callback.onSuccess()
                } catch (e: Exception) {
                    callback.onError(e)
                }
//                if (response.isSuccessful) {
//                    callback.onSuccess()
//                } else {
//                    callback.onError(IOException("HTTP ${response.code}"))
//                }
//                response.close()
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
        println("43 ${Thread.currentThread().name}")
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
        println("51 ${Thread.currentThread().name}")
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    println("52 ${Thread.currentThread().name}")
                    callback.onSuccess()
                } catch (e: Exception) {
                    callback.onError(e)
                }
            }
//                if (response.isSuccessful) {
//                    callback.onSuccess()
//                } else {
//                    callback.onError(IOException("HTTP ${response.code}"))
//                }
//                response.close()
//            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }
        })
        println("53 ${Thread.currentThread().name}")
    }
}
