package com.example.simplenote.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.Response


data class GeminiContentPart(val text: String)
data class GeminiContent(val parts: List<GeminiContentPart>)
data class GeminiRequest(val contents: List<GeminiContent>)

data class GeminiResponsePart(val text: String?)
data class GeminiResponseContent(val parts: List<GeminiResponsePart>?)
data class GeminiCandidate(val content: GeminiResponseContent?)
data class GeminiResponse(val candidates: List<GeminiCandidate>?)

interface GeminiApi {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}
