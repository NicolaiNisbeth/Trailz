package com.example.favorite

import android.util.Log
import com.example.base.Result
import com.example.base.domain.Favorite
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class FavoriteRepositoryImpl: FavoriteRepository {

    private val TAG = javaClass.name
    private val collectionPath = "/favorites"
    private val collection by lazy {
        FirebaseFirestore.getInstance().collection(collectionPath)
    }

    @ExperimentalCoroutinesApi
    override fun observeFavoriteBy(userId: String?): Flow<Result<Favorite>> {
        return if (userId != null){
            callbackFlow<Result<Favorite>> {
                trySend(Result.loading())
                val listener = collection.document(userId)
                    .addSnapshotListener { document, error ->
                        document?.let {
                            val favorite = it.toObject(Favorite::class.java)
                            if (favorite != null){
                                trySend(Result.success(favorite))
                            } else {
                                trySend(Result.failed("No favorites was found for id $userId"))
                            }
                        }
                        error?.let {
                            trySend(Result.failed(it.message.toString()))
                            Log.d(TAG, it.message.toString())
                        }
                    }
                awaitClose { listener.remove() }
            }
        } else {
            flow {  }
        }
    }

    override fun addToFavorite(favoritedId: String, userId: String?) = flow<Result<Unit>> {
        if (userId == null) return@flow

        emit(Result.loading())

        val documentReference = collection.document(userId)
        val favorite = documentReference.get()
            .await()
            .toObject(Favorite::class.java)

        if (favorite != null){
            documentReference
                .update("followedUserIds", FieldValue.arrayUnion(favoritedId))
                .await()
        } else {
            documentReference
                .set(Favorite(userId, listOf(favoritedId)))
                .await()
        }

        emit(Result.success(Unit))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

    override fun removeFromFavorite(favoritedId: String, userId: String?) = flow<Result<Unit>> {
        if (userId == null) return@flow

        emit(Result.loading())

        val documentReference = collection.document(userId)
        val favorite = documentReference.get()
            .await()
            .toObject(Favorite::class.java)

        if (favorite != null){
            documentReference
                .update("followedUserIds", FieldValue.arrayRemove(favoritedId))
                .await()
        }

        emit(Result.success(Unit))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

}