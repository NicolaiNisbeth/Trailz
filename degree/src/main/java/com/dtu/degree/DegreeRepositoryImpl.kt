package com.dtu.degree

import android.util.Log
import com.dtu.base.domain.Degree
import com.dtu.base.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class DegreeRepositoryImpl: DegreeRepository {

    private val TAG = javaClass.name
    private val collectionPath = "/degrees"
    private val collection by lazy {
        FirebaseFirestore.getInstance().collection(collectionPath)
    }

    override fun getDegrees() = flow<Result<List<Degree>>> {
        emit(Result.loading())
        val documents = collection.get().await()
        val degrees = documents.toObjects(Degree::class.java)
        emit(Result.success(degrees))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

    @ExperimentalCoroutinesApi
    override fun observeDegrees() = callbackFlow<Result<List<Degree>>> {
        trySend(Result.loading())

        val listener = collection.addSnapshotListener { documents, error ->
            documents?.let {
                val degrees = it.toObjects(Degree::class.java)
                trySend(Result.success(degrees))
            }
            error?.let {
                trySend(Result.failed(it.message.toString()))
                Log.d(TAG, it.message.toString())
            }
        }

        awaitClose { listener.remove() }
    }
}