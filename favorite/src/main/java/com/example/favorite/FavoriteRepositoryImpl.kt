package com.example.favorite

import android.util.Log
import com.example.base.Result
import com.example.base.domain.Favorite
import com.example.base.domain.StudyPlan
import com.example.base.domain.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
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

    override fun getFavoritesBy(userId: String) = flow<Result<Favorite>> {
        val document = collection.document(userId)
            .get()
            .await()

        val user = document.toObject(Favorite::class.java)
        if (user != null){
            emit(Result.success(user))
        } else {
            emit(Result.failed("No favorite was found by id=$user"))
        }
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }

    override fun addToFavorite(favoritedId: String, userId: String) = flow<Result<Unit>> {
        val favoritedStudyPlan = FirebaseFirestore.getInstance().collection("/studyplans").document(favoritedId)
        favoritedStudyPlan.update(
            "likes", FieldValue.increment(1),
            "checked", true
        ).await()

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

        emit(Result.success(Unit))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

    override fun removeFromFavorite(favoritedId: String, userId: String) = flow<Result<Unit>> {
        val favoritedStudyPlan = FirebaseFirestore.getInstance().collection("/studyplans").document(favoritedId)
        favoritedStudyPlan.update(
            "likes", FieldValue.increment(-1),
            "checked", false
        ).await()

        val documentReference = collection.document(userId)
        val favorite = documentReference.get()
            .await()
            .toObject(Favorite::class.java)

        if (favorite != null) {
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

