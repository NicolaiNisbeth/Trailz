package com.dtu.studyplan.remote

import android.util.Log
import com.dtu.base.Result
import com.dtu.base.domain.StudyPlan
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class StudyPlanRemoteImpl(
    firebaseFirestore: FirebaseFirestore
): StudyPlanRemoteDataSource {

    private val TAG = javaClass.name
    private val collectionPath = "/studyplans"
    private val collection by lazy {
        firebaseFirestore.collection(collectionPath)
    }

    @ExperimentalCoroutinesApi
    override suspend fun observeStudyPlan(id: String)= callbackFlow<Result<StudyPlan>> {
        val listener = collection.document(id).addSnapshotListener { document, error ->
            document?.let {
                val studyPlan = it.toObject(StudyPlan::class.java)
                if (studyPlan != null)
                    trySend(Result.success(studyPlan))
                else
                    trySend(Result.failed("No studyPlan with id=$id was found!"))
            }
            error?.let {
                trySend(Result.failed(it.message.toString()))
                Log.d(TAG, it.message.toString())
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun getStudyPlan(id: String): Result<StudyPlan> {
        return try {
            val document = collection.document(id).get().await()
            val studyPlan = document.toObject(StudyPlan::class.java)
            if (studyPlan != null){
                Result.success(studyPlan)
            } else {
                Result.failed("Study plan $id was not found")
            }
        } catch (e: Exception){
            Log.d(TAG, e.message.toString())
            Result.failed(e.message.toString())
        }
    }

    override suspend fun getStudyPlans() : Result<List<StudyPlan>> {
        return try {
            val documents = collection.get().await()
            val studyPlans = documents.toObjects(StudyPlan::class.java)
            Result.success(studyPlans)
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            Result.failed(e.message.toString())
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun observeStudyPlans() = callbackFlow<Result<List<StudyPlan>>> {
        val listener = collection.addSnapshotListener { documents, error ->
            documents?.let {
                val studyPlans = it.toObjects(StudyPlan::class.java)
                trySend(Result.success(studyPlans))
            }
            error?.let {
                trySend(Result.failed(it.message.toString()))
                Log.d(TAG, it.message.toString())
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun deleteStudyPlan(id: String) = flow<Result<Unit>> {
        collection.document(id).delete().await()
        emit(Result.success(Unit))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }

    override suspend fun createStudyPlan(studyPlan: StudyPlan) = flow<Result<Unit>> {
        collection.document(studyPlan.userId).set(studyPlan).await()
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }

    override suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan)= flow<Result<Unit>> {
        collection.document(id).set(studyPlan).await()
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }

    override suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean): Result<Unit> {
        return try {
            val incrementer = if (isFavorite) 1L else -1L
            collection.document(id).update("likes", FieldValue.increment(incrementer),).await()
            Result.success(Unit)
        } catch (e: Exception){
            Log.d(TAG, e.message.toString())
            Result.failed(e.message.toString())
        }
    }
}