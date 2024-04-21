package com.example.imageloading.repository

import com.example.imageloading.apiService.ApiService
import com.example.imageloading.model.ApiResponse

// ImageRepository.kt
class ImageRepository(private val apiService: ApiService) {
    suspend fun fetchImages(clientId: String, perPage: Int): ApiResponse {
        return apiService.getImages(clientId, perPage)
    }

    suspend fun fetchMoreImages(clientId: String, perPage: Int, page: Int): ApiResponse {
        return apiService.getMoreImages(clientId, perPage, page)
    }

}
