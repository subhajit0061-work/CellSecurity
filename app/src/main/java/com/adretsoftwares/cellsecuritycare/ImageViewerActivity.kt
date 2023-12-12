package com.adretsoftwares.cellsecuritycare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.adretsoftwares.cellsecuritycare.common.Constants
import com.adretsoftwares.cellsecuritycare.databinding.ActivityImageViewerBinding
import com.adretsoftwares.cellsecuritycare.util.DownloadImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding
    private var imageUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.let {
            val imageLoader = CustomVolleyRequest.getInstance(this).imageLoader
            imageUrl = it.getString(Constants.IMAGE_URL_INTENT)
            binding.networkImageView.setImageUrl(imageUrl, imageLoader)
        }

        binding.btnDownload.setOnClickListener {
            imageUrl?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val imageFile = DownloadImage.mLoad(it, this@ImageViewerActivity);
                        DownloadImage.mSaveMediaToStorage(imageFile, this@ImageViewerActivity);
                    } catch (e: Exception) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}