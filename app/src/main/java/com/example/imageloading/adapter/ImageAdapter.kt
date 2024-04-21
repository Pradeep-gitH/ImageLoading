package com.example.imageloading.adapter

import ProfileImage
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.imageloading.R
import com.example.imageloading.model.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL

class ImageAdapter(private val context: Context, private var images: ApiResponse) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        image.user.profile_image.loadBitmap() // Load bitmap from cache
        if (image.user.profile_image._bitmap != null) {
            holder.imageView.setImageBitmap(image.user.profile_image._bitmap)
        } else {
            loadImageWithCoroutines(holder.imageView, image.user.profile_image)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    private fun loadImageWithCoroutines(imageView: ImageView, image: ProfileImage) {

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val imageUrl = image.medium
                val inputStream = URL(imageUrl).openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                image._bitmap = bitmap
                image.saveBitmap()
                launch(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    imageView.setImageResource(R.drawable.error_image) // Error placeholder
                }
            }
        }
    }
    fun updateData(response: ApiResponse) {
        images = response
        notifyDataSetChanged()
    }

    fun addImages(newImages: ApiResponse) {
        val startPosition = images.size
        images.addAll(newImages)
        notifyItemRangeInserted(startPosition, newImages.size)
    }
}
