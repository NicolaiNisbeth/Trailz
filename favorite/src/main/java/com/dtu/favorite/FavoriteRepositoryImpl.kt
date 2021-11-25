package com.dtu.favorite

import android.util.Log
import com.dtu.base.Result
import com.dtu.base.domain.Favorite
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class FavoriteRepositoryImpl : FavoriteRepository {

    private val TAG = javaClass.name
    private val collectionPath = "/favorites"
    private val collection by lazy {
        FirebaseFirestore.getInstance().collection(collectionPath)
    }

    @ExperimentalCoroutinesApi
    override fun observeFavoriteBy(userId: String) = callbackFlow<Result<Favorite>> {
        val listener = collection.document(userId)
            .addSnapshotListener { document, error ->
                document?.let {
                    val favorite = it.toObject(Favorite::class.java)
                    if (favorite != null) {
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

    override suspend fun getFavoritesBy(userId: String): Result<Favorite> {
        return try {
            val document = collection.document(userId)
                .get()
                .await()
            val user = document.toObject(Favorite::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failed("No favorite was found by id=$user")
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            Result.failed(e.message.toString())
        }
    }

    override suspend fun addToFavorite(favoritedId: String, userId: String): Result<Unit> {
        return try {
            val documentReference = collection.document(userId)
            val favorite = documentReference.get()
                .await()
                .toObject(Favorite::class.java)

            if (favorite != null) {
                documentReference
                    .update("followedUserIds", FieldValue.arrayUnion(favoritedId))
                    .await()
            } else {
                documentReference
                    .set(Favorite(userId, listOf(favoritedId)))
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            Result.failed(e.message.toString())
        }
    }
    override suspend fun removeFromFavorite(favoritedId: String, userId: String): Result<Unit> {
        return try {
            val documentReference = collection.document(userId)
            val favorite = documentReference.get()
                .await()
                .toObject(Favorite::class.java)

            if (favorite != null) {
                documentReference
                    .update("followedUserIds", FieldValue.arrayRemove(favoritedId))
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception){
            Log.d(TAG, e.message.toString())
            Result.failed(e.message.toString())
        }
    }
}

