package com.example.studyplan.remote

import android.util.Log
import com.example.base.Result
import com.example.base.domain.StudyPlan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class StudyPlanRemoteImpl: StudyPlanRemoteDataSource {

    private val TAG = javaClass.name
    private val collectionPath = "/studyplans"
    private val collection by lazy {
        FirebaseFirestore.getInstance().collection(collectionPath)
    }

    override suspend fun getStudyPlan(id: String) = flow<Result<StudyPlan>> {
        val document = collection.document(id).get().await()
        val studyPlan = document.toObject(StudyPlan::class.java)
        if (studyPlan != null){
            emit(Result.success(studyPlan))
        } else {
            emit(Result.failed("Study plan $id was not found"))
        }
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }

    override suspend fun getStudyPlans()= flow<Result<List<StudyPlan>>> {
        val documents = collection.get().await()
        val studyPlans = documents.toObjects(StudyPlan::class.java)
        emit(Result.success(studyPlans))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
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
}