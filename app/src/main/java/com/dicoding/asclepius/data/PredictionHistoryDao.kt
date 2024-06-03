package com.dicoding.asclepius.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.dicoding.asclepius.model.PredictionHistory

@Dao
interface PredictionHistoryDao {
    @Insert
    fun insert(predictionHistory: PredictionHistory)

    @Delete
    fun delete(predictionHistory: PredictionHistory)

    @Query("SELECT * FROM prediction_history")
    fun getAllPredictionHistory(): List<PredictionHistory>
}
