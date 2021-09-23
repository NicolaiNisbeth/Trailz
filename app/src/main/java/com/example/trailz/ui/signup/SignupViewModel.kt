package com.example.trailz.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailz.inmemory.ActiveUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _studyPath = MutableLiveData<String>()
    val studyPath: LiveData<String> = _studyPath

    private val _studyPaths = MutableLiveData<List<String>>()
    val studyPaths: LiveData<List<String>> = _studyPaths

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    private val _signupSuccess = MutableLiveData<Boolean>()
    val signupSuccess: LiveData<Boolean> = _signupSuccess

    init {
        viewModelScope.launch {
            _studyPaths.value = listOf(
                "Softwareteknologi",
                "Produktion",
                "Elektroteknologi",
            )
        }
    }

    fun changeUsername(username: String){
        _username.value = username
    }

    fun changeEmail(email: String){
        _email.value = email
    }

    fun changePassword(password: String){
        _password.value = password
    }

    fun changeStudyPath(studyPath: String){
        _studyPath.value = studyPath
    }

    fun signup(){
        val username = username.value
        val email = email.value
        val password = password.value
        val studyPath = studyPath.value
        val isInputInvalid = username.isNullOrBlank()
                || email.isNullOrBlank()
                || password.isNullOrBlank()
                || studyPath.isNullOrBlank()

        if (isInputInvalid){
            _error.value = true
            return
        }

        viewModelScope.launch {
            _loading.value = true
            val userId = createUserUseCase(User(username!!, email!!, password!!, studyPath!!))
            if (userId != null){
                _signupSuccess.value = true
                ActiveUser.setId(userId)
            }
            _loading.value = false
        }

    }
}