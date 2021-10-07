package com.example.user

import com.example.base.domain.User
import kotlinx.coroutines.flow.Flow
import com.example.base.Result

interface UserRepository {

    fun signIn(email: String, password: String): Flow<Result<String>>

    fun signUp(user: User): Flow<Result<String>>

    fun getUserBy(id: String): Flow<Result<User>>

    fun observeUserBy(id: String): Flow<Result<User>>

}