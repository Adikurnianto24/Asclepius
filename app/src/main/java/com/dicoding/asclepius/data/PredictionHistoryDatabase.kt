package com.dicoding.asclepius.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.asclepius.model.PredictionHistory

@Database(entities = [PredictionHistory::class], version = 1)
abstract class PredictionHistoryDatabase : RoomDatabase() {
    abstract fun predictionHistoryDao(): PredictionHistoryDao
}
