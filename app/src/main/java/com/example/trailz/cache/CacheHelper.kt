package com.example.trailz.cache

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.studyplan.CacheUtil

class CacheHelper(private val context: Context): CacheUtil {

    override fun isDataStale(key: String): Boolean {
        val currentMillis = System.currentTimeMillis()
        val lastFetchTimeMS = PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0)
        val oneHourMS = 60 * 60 * 1000
        val isOneHourAgo = currentMillis > lastFetchTimeMS + oneHourMS
        return isOneHourAgo
    }

    override fun updateTimer(key: String, timerInMillis: Long) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putLong(key, timerInMillis)
        }
    }
}