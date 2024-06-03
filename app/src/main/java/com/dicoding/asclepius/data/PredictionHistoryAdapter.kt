package com.dicoding.asclepius.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.model.PredictionHistory
import com.dicoding.asclepius.databinding.ItemPredictionHistoryBinding


class PredictionHistoryAdapter(
    private var predictionHistoryList: List<PredictionHistory>,
    private val listener: OnDeleteItemClickListener
) : RecyclerView.Adapter<PredictionHistoryAdapter.PredictionHistoryViewHolder>() {

    interface OnDeleteItemClickListener {
        fun onDeleteItemClicked(predictionHistory: PredictionHistory)
    }

    inner class PredictionHistoryViewHolder(private val binding: ItemPredictionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(predictionHistory: PredictionHistory) {
            Glide.with(binding.root)
                .load(predictionHistory.imageUri)
                .skipMemoryCache(true)
                .into(binding.ImageUrl)

            binding.textPredictionResult.text = predictionHistory.predictionResult
            binding.textConfidenceScore.text = String.format("%.2f", predictionHistory.confidenceScore)

            binding.btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedPredictionHistory = predictionHistoryList[position]
                    listener.onDeleteItemClicked(clickedPredictionHistory)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionHistoryViewHolder {
        val binding = ItemPredictionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PredictionHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionHistoryViewHolder, position: Int) {
        holder.bind(predictionHistoryList[position])
    }

    override fun getItemCount(): Int {
        return predictionHistoryList.size
    }


    fun updateData(newPredictionHistoryList: List<PredictionHistory>) {
        predictionHistoryList = newPredictionHistoryList
        notifyDataSetChanged()
    }
}

