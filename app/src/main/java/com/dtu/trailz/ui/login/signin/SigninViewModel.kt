package com.dtu.trailz.ui.login.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dtu.base.Result
import com.dtu.trailz.inject.SharedPrefs
import com.dtu.trailz.ui.common.compose.invalidInput
import com.dtu.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SigninViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sharedPrefs: SharedPrefs
): ViewModel() {

    // FIXME: hardcoded email
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    // FIXME: hardcoded password
    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private val _signinSuccess = MutableLiveData<Boolean>()
    val signinSuccess: LiveData<Boolean> = _signinSuccess

    fun changeEmail(email: String){
        _email.value = email
    }

    fun changePassword(password: String){
        _password.value = password
    }

    fun signIn(){
        val email = email.value
        val password = password.value
        if (invalidInput(email, password)){
            _error.value = true
            return
        }

        viewModelScope.launch {
            repository.signIn(email!!, password!!).collect {
                when (it){
                    is Result.Failed -> {
                        _error.value = true
                        _loading.value = false
                    }
                    is Result.Loading -> _loading.value = true
                    is Result.Success -> {
                        _signinSuccess.value = true
                        sharedPrefs.loggedInId = it.data
                    }
                }
            }
        }
    }
}