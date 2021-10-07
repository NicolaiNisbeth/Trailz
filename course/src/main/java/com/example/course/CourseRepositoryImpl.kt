package com.example.course

import android.util.Log
import com.example.base.Result
import com.example.base.domain.Course
import com.example.base.domain.Favorite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class CourseRepositoryImpl: CourseRepository {

    private val TAG = javaClass.name
    private val collectionPath = "/courses"
    private val collection by lazy {
        FirebaseFirestore.getInstance().collection(collectionPath)
    }

    override fun getCourses() = flow<Result<List<Course>>> {
        emit(Result.loading())
        val documents = collection.get().await()
        val courses = documents.toObjects(Course::class.java)
        emit(Result.success(courses))
    }.catch {
        emit(Result.failed(it.message.toString()))
        Log.d(TAG, it.message.toString())
    }.flowOn(Dispatchers.IO)

    @ExperimentalCoroutinesApi
    override fun observeCourses() = callbackFlow<Result<List<Course>>> {
        trySend(Result.loading())
        val listener = collection.addSnapshotListener { documents, error ->
            documents?.let {
                val courses = it.toObjects(Course::class.java)
                trySend(Result.success(courses))
            }
            error?.let {
                trySend(Result.failed(it.message.toString()))
                Log.d(TAG, it.message.toString())
            }
        }
        awaitClose { listener.remove() }
    }
}