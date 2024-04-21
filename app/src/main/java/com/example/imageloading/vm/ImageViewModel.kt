package com.example.imageloading.vm// ImageViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imageloading.model.ApiResponse
import com.example.imageloading.repository.ImageRepository
import kotlinx.coroutines.launch

class ImageViewModel(private val repository: ImageRepository) : ViewModel() {
    private val _images = MutableLiveData<ApiResponse>()
    val images: LiveData<ApiResponse> = _images
    fun fetchImages(clientId: String, perPage: Int) {
        viewModelScope.launch {
            try {
                val result = repository.fetchImages(clientId, perPage)
                _images.postValue(result)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    fun fetchMoreImages(clientId: String, perPage: Int, page: Int) {
        viewModelScope.launch {
            try {
                val result = repository.fetchMoreImages(clientId, perPage, page)
                _images.postValue(result)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

}
