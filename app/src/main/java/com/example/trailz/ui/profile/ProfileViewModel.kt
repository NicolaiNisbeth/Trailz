package com.example.trailz.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.signup.GetUserUseCase
import com.example.trailz.ui.signup.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isLoggedIn = user != null && !isLoading
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableLiveData<ProfileUiState>()
    val state: LiveData<ProfileUiState> = _state

    fun logout(){
        sharedPrefs.loggedInId = null
        _state.value = ProfileUiState()
    }

    fun getUser(){
        val loggedInId = sharedPrefs.loggedInId
        if (loggedInId.isNullOrBlank()) {
            // default state is logged out
            _state.value = ProfileUiState()
            return
        }

        viewModelScope.launch {
            ProfileUiState(isLoading = true)
            getUserUseCase(loggedInId, object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _state.value = ProfileUiState(User(email = snapshot.value.toString()))
                }

                override fun onCancelled(error: DatabaseError) {
                    _state.value = ProfileUiState(error = error.message)
                }
            })
        }
    }
}
