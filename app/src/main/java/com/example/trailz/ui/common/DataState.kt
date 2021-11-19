package com.example.trailz.ui.common

data class DataState<out T>(
    val data: T? = null,
    val exception: String? = null,
    val isEmpty: Boolean = false,
    val isLoading: Boolean = false
)