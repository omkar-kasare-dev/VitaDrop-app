package com.social.vitadrop.data.ai

import android.util.Log
import com.example.kotlinbasics.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

class GeminiManager {

    init {

        Log.d(
            "GEMINI_TEST",
            BuildConfig.GEMINI_API_KEY
        )
    }

    private val generativeModel =
        GenerativeModel(

            modelName = "gemini-2.0-flash",

            apiKey = BuildConfig.GEMINI_API_KEY
        )


    suspend fun generateResponse(
        prompt: String
    ): String {

        return try {

            val response =
                generativeModel.generateContent(prompt)

            response.text ?: "No Response"

        } catch (e: Exception) {

            e.message ?: "Something went wrong"
        }
    }
}