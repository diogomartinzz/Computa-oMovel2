package com.example.aiapp.data.repository

import com.example.aiapp.data.api.ImageApiService
import com.example.aiapp.data.model.ImageItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository(private val apiService: ImageApiService) {
    suspend fun getImages(): Result<List<ImageItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getImages()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception("Response body is null"))
                    }
                } else {
                    Result.failure(Exception("API error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
