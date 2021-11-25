package com.dtu.studyplan

interface CacheUtil {
    fun isDataStale(key: String): Boolean
    fun updateTimer(key: String, timerInMillis: Long)
}