package com.dtu.user

import com.dtu.base.domain.User
import kotlinx.coroutines.flow.Flow
import com.dtu.base.Result

interface UserRepository {

    fun signIn(email: String, password: String): Flow<Result<String>>

    fun signUp(user: User): Flow<Result<String>>

    fun getUserBy(id: String): Flow<Result<User>>

    fun observeUserBy(id: String): Flow<Result<User>>

}