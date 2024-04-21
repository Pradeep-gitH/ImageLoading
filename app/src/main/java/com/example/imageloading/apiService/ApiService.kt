package com.example.imageloading.apiService// ApiService.kt
import com.example.imageloading.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("photos")
    suspend fun getImages(
        @Query("client_id") clientId: String,
        @Query("per_page") perPage: Int
    ): ApiResponse

    @GET("photos")
    suspend fun getMoreImages(
        @Query("client_id") clientId: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): ApiResponse
}
