package com.example.trailz.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailz.inmemory.ActiveUser
import com.example.trailz.ui.signup.GetUserUseCase
import com.example.trailz.ui.signup.User
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    val userId: LiveData<String?> = ActiveUser.id

    fun getUser(id: String, listener: ValueEventListener ){
        viewModelScope.launch {
            getUserUseCase(id, listener)
        }
    }

}