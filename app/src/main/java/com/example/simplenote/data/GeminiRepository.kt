package com.example.simplenote.data

import android.content.Context
import com.example.simplenote.data.remote.GeminiApi
import com.example.simplenote.data.remote.GeminiContent
import com.example.simplenote.data.remote.GeminiContentPart
import com.example.simplenote.data.remote.GeminiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(private val context: Context, private val geminiApi: GeminiApi) {
    private val apiKey: String by lazy {
        val appInfo = context.packageManager.getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
        appInfo.metaData?.getString("gemini.apikey") ?: throw IllegalStateException("Gemini API key not found in manifest")
    }

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
