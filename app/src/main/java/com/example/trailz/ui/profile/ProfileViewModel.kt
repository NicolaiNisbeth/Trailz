package com.example.trailz.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.Result
import com.example.base.domain.User
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.signup.GetUserUseCase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.user.UserRepository
import kotlinx.coroutines.flow.collect

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isLoggedIn = user != null
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableLiveData<ProfileUiState>()
    val state: LiveData<ProfileUiState> = _state

    fun logout(){
        sharedPrefs.loggedInId = null
        _state.value = ProfileUiState()
    }

    fun getUser(userId: String?){
        if (userId.isNullOrBlank()) {
            // default Ui state is logged out
            _state.value = ProfileUiState()
            return
        }

        viewModelScope.launch {
            repository.getUserBy(userId).collect {
                _state.value = when (it){
                    is Result.Loading -> ProfileUiState(isLoading = true)
                    is Result.Failed -> ProfileUiState(error = it.message)
                    is Result.Success -> ProfileUiState(user = it.data)
                }
            }
        }
    }
}
