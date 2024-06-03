package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.data.PredictionHistoryDao
import com.dicoding.asclepius.data.DatabaseBuilder
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.model.PredictionHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import com.dicoding.asclepius.R

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var predictionHistoryDao: PredictionHistoryDao
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        predictionHistoryDao = DatabaseBuilder.getInstance(applicationContext).predictionHistoryDao()

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val imageUri = Uri.parse(imageUriString ?: "")

        try {
            imageUri?.let {
                Log.d(TAG, "ShowImage: $it")
                binding.resultImage.setImageURI(it)

                val imageClassifierHelper = ImageClassifierHelper(
                    context = this,
                    classifierListener = object : ImageClassifierHelper.ClassifierListener {
                        override fun onError(error: String) {
                            showErrorMessage("Terjadi kesalahan: $error")
                        }

                        @SuppressLint("SetTextI18n")
                        override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                            results?.let {
                                val topResult = it[0]
                                val label = topResult.categories[0].label
                                val score = topResult.categories[0].score

                                fun Float.formatToString(): String {
                                    return String.format("%.2f%%", this * 100)
                                }
                                binding.resultText.text = "$label ${score.formatToString()}"

                                CoroutineScope(Dispatchers.IO).launch {
                                    savePredictionToDatabase(imageUriString ?: "", label, score)
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(this@ResultActivity, "Data berhasil dianalyze", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                )
                imageClassifierHelper.classifyStaticImage(imageUri)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image: ${e.message}", e)
            showErrorMessage("Terjadi kesalahan saat memproses gambar")
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun savePredictionToDatabase(imageUri: String, label: String, score: Float) {
        val confidenceScore = score * 100

        val predictionHistory = PredictionHistory(imageUri = imageUri, predictionResult = label, confidenceScore = confidenceScore)
        predictionHistoryDao.insert(predictionHistory)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_img_uri"
        const val TAG = "imagePicker"
    }
}