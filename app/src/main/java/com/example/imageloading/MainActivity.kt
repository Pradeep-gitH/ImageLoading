package com.example.imageloading

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageloading.adapter.ImageAdapter
import com.example.imageloading.apiService.ApiClient
import com.example.imageloading.constant.Constant.accessKey
import com.example.imageloading.databinding.ActivityMainBinding
import com.example.imageloading.model.ApiResponse
import com.example.imageloading.repository.ImageRepository
import com.example.imageloading.vm.ImageViewModel
import com.example.imageloading.vm.ImageViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var imageAdapter: ImageAdapter
    private var isLoading = false
    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiService = ApiClient.create()
        val repository = ImageRepository(apiService) // Initialize your repository as needed
        val viewModelFactory = ImageViewModelFactory(repository)
        imageViewModel = ViewModelProvider(this, viewModelFactory)[ImageViewModel::class.java]
        // Initialize RecyclerView and adapter
        imageAdapter = ImageAdapter(this, ApiResponse())

        imageViewModel.images.observe(this, Observer { image ->
            imageAdapter.updateData(image)
        })
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = imageAdapter
        }

        imageViewModel.fetchImages(accessKey, 20)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                    loadMoreData()
                }
            }
        })

    }
    private fun loadMoreData() {
        if (!isLoading) {
            isLoading = true
            imageViewModel.fetchMoreImages(accessKey, 10, currentPage+1)
        }
    }
}
