package com.example.trailz.inmemory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// FIXME: Save it in shared pref but make it a observable cache
object ActiveUser {
    private var _id = MutableLiveData<String?>()
    val id: LiveData<String?> = _id

    fun setId(id: String){
        _id.value = id
    }
}