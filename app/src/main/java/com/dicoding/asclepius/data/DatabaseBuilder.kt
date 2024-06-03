package com.dicoding.asclepius.data

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {
    private var INSTANCE: PredictionHistoryDatabase? = null

    fun getInstance(context: Context): PredictionHistoryDatabase {
        if (INSTANCE == null) {
            synchronized(PredictionHistoryDatabase::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PredictionHistoryDatabase::class.java,
                        "prediction_history_database"
                    ).build()
                }
            }
        }
        return INSTANCE!!
    }
}
