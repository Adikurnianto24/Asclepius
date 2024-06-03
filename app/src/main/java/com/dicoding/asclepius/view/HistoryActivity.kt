package com.dicoding.asclepius.view

import android.widget.ImageView
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.PredictionHistoryDao
import com.dicoding.asclepius.data.DatabaseBuilder
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.model.PredictionHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.dicoding.asclepius.data.PredictionHistoryAdapter
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity(), PredictionHistoryAdapter.OnDeleteItemClickListener {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var predictionHistoryDao: PredictionHistoryDao
    private lateinit var historyAdapter: PredictionHistoryAdapter
    private lateinit var imgEmptyView: ImageView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        predictionHistoryDao = DatabaseBuilder.getInstance(applicationContext).predictionHistoryDao()

        recyclerView = binding.recyclerView
        imgEmptyView = binding.imgEmptyView

        setupRecyclerView()
        loadPredictionHistory()
        binding.btnBack.setOnClickListener {
            onBackPressed()

        }
    }

    private fun setupRecyclerView() {
        historyAdapter = PredictionHistoryAdapter(emptyList(), this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }
    }

    private fun loadPredictionHistory() {
        lifecycleScope.launch {
            try {
                val predictionHistoryList = fetchPredictionHistoryFromDatabase()
                historyAdapter.updateData(predictionHistoryList)
                updateEmptyViewVisibility()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading prediction history: ${e.message}")
                showToast("Gagal memuat riwayat prediksi")
            }
        }
    }

    private suspend fun fetchPredictionHistoryFromDatabase(): List<PredictionHistory> {
        return withContext(Dispatchers.IO) {
            predictionHistoryDao.getAllPredictionHistory()
        }
    }

    override fun onDeleteItemClicked(predictionHistory: PredictionHistory) {
        lifecycleScope.launch {
            try {
                deletePredictionHistory(predictionHistory)
                loadPredictionHistory()
                showToast("Data berhasil dihapus")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting prediction history: ${e.message}")
                showToast("Gagal menghapus data")
            }
        }
    }

    private suspend fun deletePredictionHistory(predictionHistory: PredictionHistory) {
        withContext(Dispatchers.IO) {
            predictionHistoryDao.delete(predictionHistory)
        }
    }
    private fun updateEmptyViewVisibility() {
        if (historyAdapter.itemCount == 0) {
            imgEmptyView.visibility = ImageView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            imgEmptyView.visibility = ImageView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "HistoryActivity"
    }
}
