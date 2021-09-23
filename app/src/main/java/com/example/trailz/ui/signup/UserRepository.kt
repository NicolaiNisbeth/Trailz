package com.example.trailz.ui.signup

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

data class User(
    val username: String,
    val email: String,
    val password: String,
    val studyPath: String
)

class CreateUserUseCase(
    private val repository: UserRepository
){
    suspend operator fun invoke(user: User) = withContext(Dispatchers.IO){
        repository.createUser(user)
    }
}

class GetUserUseCase(
    private val repository: UserRepository
){
    suspend operator fun invoke(email: String, listener: ValueEventListener) = withContext(Dispatchers.IO){
        repository.getUser(email, listener)
    }
}

class DeleteUserUseCase(
    private val repository: UserRepository
){
    suspend operator fun invoke(email: String) = withContext(Dispatchers.IO){
        repository.deleteUser(email)
    }
}

interface UserRepository {
    fun createUser(user: User): String?
    fun getUser(email: String, listener: ValueEventListener)
    fun deleteUser(email: String): Boolean
}

class UserRepositoryImpl(
    private val userService: UserService
): UserRepository {

    override fun createUser(user: User) = userService.createUser(user)

    override fun deleteUser(email: String) = userService.deleteUser(email)

    override fun getUser(
        email: String,
        listener: ValueEventListener
    ) = userService.getUser(email, listener)
}

interface UserService {
    fun createUser(user: User): String?
    fun getUser(email: String, listener: ValueEventListener)
    fun deleteUser(email: String): Boolean
}

class UserFirebase(
    private val databaseReference: FirebaseDatabase
): UserService {



    override fun createUser(user: User): String? {
        val key = databaseReference.getReference("users").push()
        key.setValue(user)
        return key.key
    }

    override fun getUser(email: String, listener: ValueEventListener) {
        databaseReference.getReference("users")
            .child(email)
            .addListenerForSingleValueEvent(listener)
    }

    override fun deleteUser(email: String): Boolean {
        databaseReference.getReference("users")
            .child(email)
            .setValue(null)
        return true
    }

}
