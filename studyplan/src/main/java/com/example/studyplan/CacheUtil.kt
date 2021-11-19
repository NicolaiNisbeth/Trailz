package com.example.studyplan

interface CacheUtil {
    fun isDataStale(key: String, currentMillis: Long): Boolean
    fun updateTimer(key: String, timerInMillis: Long)
}