package com.example.trailz

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TrailzApplication(
): Application(){
}