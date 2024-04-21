import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.jakewharton.disklrucache.DiskLruCache
import java.io.File
import java.io.IOException

data class ProfileImage(
    val large: String,
    val medium: String,
    val small: String
) {
     var _bitmap: Bitmap? = null

    companion object {
        private const val MEMORY_CACHE_SIZE = 10 // Adjust as needed
        private val memoryCache = mutableMapOf<String, Bitmap>()
        private lateinit var diskCache: DiskLruCache

        init {
            // Initialize disk cache
            try {
                val cacheDir = File(Environment.getExternalStorageDirectory(), "image_cache")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }
                diskCache = DiskLruCache.open(cacheDir, 1, 1, 10 * 1024 * 1024) // 10MB cache size
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun getBitmapFromMemoryCache(imageUrl: String): Bitmap? {
            return memoryCache[imageUrl]
        }

        fun putBitmapInMemoryCache(imageUrl: String, bitmap: Bitmap) {
            memoryCache[imageUrl] = bitmap
        }

        fun getBitmapFromDiskCache(imageUrl: String): Bitmap? {
            if (!this::diskCache.isInitialized) {
                // Disk cache is not initialized, handle it accordingly
                return null
            }

            val key = imageUrl.hashCode().toString()
            val snapshot = diskCache[key]
            return snapshot?.let {
                val fileInputStream = it.getInputStream(0)
                BitmapFactory.decodeStream(fileInputStream)
            }
        }

        fun putBitmapInDiskCache(imageUrl: String, bitmap: Bitmap) {
            if (this::diskCache.isInitialized){
                val key = imageUrl.hashCode().toString()
                var editor: DiskLruCache.Editor? = null
                try {
                    editor = diskCache.edit(key)
                    editor?.newOutputStream(0)
                        ?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                    editor?.commit()
                } catch (e: IOException) {
                    e.printStackTrace()
                    editor?.abort()
                }
            }

        }
    }

    fun loadBitmap() {
        _bitmap = getBitmapFromMemoryCache(medium) ?: getBitmapFromDiskCache(medium)
    }

    fun saveBitmap() {
        _bitmap?.let {
            putBitmapInMemoryCache(medium, it)
            putBitmapInDiskCache(medium, it)
        }
    }
}
