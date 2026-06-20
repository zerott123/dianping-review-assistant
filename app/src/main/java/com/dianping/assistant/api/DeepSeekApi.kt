package com.dianping.assistant.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Serializable
data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.8,
    val max_tokens: Int = 600
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ChatMessage
)

class DeepSeekApi(private val apiKey: String) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    suspend fun generateReview(systemPrompt: String, userPrompt: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ChatRequest(
                    model = "deepseek-chat",
                    messages = listOf(
                        ChatMessage("system", systemPrompt),
                        ChatMessage("user", userPrompt)
                    ),
                    temperature = 0.8,
                    max_tokens = 600
                )

                val body = json.encodeToString(request)
                    .toRequestBody("application/json".toMediaType())

                val httpRequest = Request.Builder()
                    .url("https://api.deepseek.com/chat/completions")
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android) DeepSeekClient/1.0")
                    .post(body)
                    .build()

                val response = client.newCall(httpRequest).execute()

                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: ""
                    Result.failure(Exception("请求失败 HTTP ${response.code}: ${errorBody.take(200)}"))
                } else {
                    val responseBody = response.body?.string() ?: ""
                    val chatResponse = json.decodeFromString<ChatResponse>(responseBody)
                    val content = chatResponse.choices.firstOrNull()?.message?.content
                        ?: ""
                    Result.success(content.trim())
                }
            } catch (e: Exception) {
                Result.failure(Exception("网络异常: ${e.javaClass.simpleName} - ${e.message}"))
            }
        }
    }
}
