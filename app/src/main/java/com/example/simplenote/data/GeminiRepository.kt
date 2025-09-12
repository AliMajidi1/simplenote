package com.example.simplenote.data

import com.example.simplenote.data.remote.GeminiApi
import com.example.simplenote.data.remote.GeminiContent
import com.example.simplenote.data.remote.GeminiContentPart
import com.example.simplenote.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(private val apiKey: String, private val geminiApi: GeminiApi) {
    suspend fun generateContent(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiContentPart(text = prompt)))
                )
            )
            val response = geminiApi.generateContent(apiKey, request)
            if (response.isSuccessful) {
                val body = response.body()
                val text = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    Result.success(text)
                } else {
                    Result.failure(Exception("No AI result returned"))
                }
            } else {
                Result.failure(Exception("AI request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
