package com.example.trailz

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrailzApplication: Application(){
    var isDark by mutableStateOf(false)
}