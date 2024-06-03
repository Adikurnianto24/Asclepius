package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentImageUri = it
                showImage()
            } ?: run {
                Log.d(TAG, "No Media Selected")
                showToast(getString(R.string.unselect_images))
            }
        }

    private val launcherHistory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val analyzedImageUri = result.data?.getStringExtra("analyzed_image_uri")
                analyzedImageUri?.let {
                    currentImageUri = Uri.parse(analyzedImageUri)
                    showImage()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage()
            } ?: run {
                showToast(getString(R.string.no_images))
            }
        }

        binding.historyButton.setOnClickListener {
            viewHistory()
        }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            Glide.with(this)
                .load(uri)
                .into(binding.previewImageView)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, it.toString())
            launcherHistory.launch(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun viewHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        launcherHistory.launch(intent)
    }

    companion object {
        const val TAG = "ImagePicker"
        const val HISTORY_REQUEST_CODE = 101
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == HISTORY_REQUEST_CODE) {
                val analyzedImageUri = data?.getStringExtra("analyzed_image_uri")
                analyzedImageUri?.let {
                    currentImageUri = Uri.parse(analyzedImageUri)
                    showImage()
                }
            }
        }
    }
}
