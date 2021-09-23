package com.example.trailz.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.common.compose.invalidInput
import com.example.trailz.ui.signup.BecomeUserUseCase
import com.example.trailz.ui.signup.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SigninViewModel @Inject constructor(
    private val becomeUserUseCase: BecomeUserUseCase,
    private val sharedPrefs: SharedPrefs
): ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

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

    fun signin(){
        val email = email.value
        val password = password.value
        if (invalidInput(email, password)){
            _error.value = true
            return
        }

        viewModelScope.launch {
            _loading.value = true
            becomeUserUseCase(validateUser)
            _loading.value = false
        }
    }

    private val validateUser = object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val userExists = snapshot.children.find {
                val user = it.getValue(User::class.java)
                user?.email == email.value && user?.password == password.value
            }
            sharedPrefs.loggedInId = userExists?.key
            _signinSuccess.value = userExists != null
            _error.value = userExists == null
        }
        override fun onCancelled(error: DatabaseError) { _error.value = true }
    }
}