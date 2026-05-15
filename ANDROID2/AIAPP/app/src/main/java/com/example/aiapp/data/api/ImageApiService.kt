package com.example.aiapp.data.api

import com.example.aiapp.data.model.ImageItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApiService {
    @GET("v2/list")
    suspend fun getImages(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): Response<List<ImageItem>>
}
