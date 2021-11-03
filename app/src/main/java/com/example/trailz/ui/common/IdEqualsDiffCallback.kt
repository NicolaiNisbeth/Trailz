package com.example.trailz.ui.common

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class IdEqualsDiffCallback<T : Any, R>(
    private val idFunction: (T) -> R
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return false
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return false
    }
}