package com.example.user

import android.util.Log
import com.example.base.domain.User
import com.google.firebase.firestore.FirebaseFirestore
import com.example.base.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl: UserRepository {

    private val TAG = javaClass.name
    private val collectionPath = "/users"
    private val collection by lazy {
        FirebaseFirestore.getInstance().collection(collectionPath)
    }

    override fun signIn(email: String, password: String) = flow<Result<String>> {
        emit(Result.loading())

        val users = collection
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .limit(1)
            .get()
            .await()

        val id = users.documents.map { it.id }.firstOrNull()
        if (id != null){
            emit(Result.success(id))
        } else {
            emit(Result.failed("email=$email or password=$password does not exist"))
        }
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

    override fun signUp(user: User) = flow<Result<String>> {
        emit(Result.loading())

        val document = collection
            .add(user)
            .await()

        emit(Result.success(document.id))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

    override fun getUserBy(id: String) = flow<Result<User>> {
        emit(Result.loading())

        val document = collection.document(id)
            .get()
            .await()

        val user = document.toObject(User::class.java)
        if (user != null){
            emit(Result.success(user))
        } else {
            emit(Result.failed("No user was found by id=$id"))
        }
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

    @ExperimentalCoroutinesApi
    override fun observeUserBy(id: String) = callbackFlow<Result<User>> {
        trySend(Result.loading())

        val listener = collection.document(id).addSnapshotListener { document, error ->
            document?.let {
                val user = it.toObject(User::class.java)
                if (user != null){
                    trySend(Result.success(user))
                } else {
                    trySend(Result.failed("No user was found by id=$id"))
                }
            }
            error?.let {
                trySend(Result.failed(it.message.toString()))
                Log.d(TAG, it.message.toString())
            }
        }

        awaitClose { listener.remove() }
    }
}