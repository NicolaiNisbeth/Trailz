package com.dtu.trailz.ui.login.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dtu.base.Result
import com.dtu.base.domain.User
import com.dtu.trailz.inject.SharedPrefs
import com.dtu.trailz.ui.common.compose.invalidInput
import com.dtu.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _studyPath = MutableLiveData<String>("Software technology")
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
                "Software technology",
                "Production",
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

    fun signUp(){
        val username = username.value
        val email = email.value
        val password = password.value
        val studyPath = studyPath.value

        if (invalidInput(username, email, password, studyPath)){
            _error.value = true
            return
        }

        viewModelScope.launch {
            val user = User(username!!, email!!, password!!, studyPath!!)
            repository.signUp(user).collect {
                when (it){
                    is Result.Failed -> _error.value = true
                    is Result.Loading -> _loading.value = true
                    is Result.Success -> {
                        sharedPrefs.loggedInId = it.data
                        _signupSuccess.value = true
                    }
                }
            }
        }
    }
}