package com.example.studyplan

import com.example.studyplan.domain.StudyPlan
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow

interface StudyPlanRepository {
    val collectionPath: String
    val collection: CollectionReference

    suspend fun getStudyPlan(id: String): Flow<Result<StudyPlan>>

    suspend fun getStudyPlans(): Flow<Result<List<StudyPlan>>>

    suspend fun observeStudyPlans(): Flow<Result<List<StudyPlan>>>

    suspend fun deleteStudyPlan(id: String): Flow<Result<Unit>>

    suspend fun createStudyPlan(studyPlan: StudyPlan): Flow<Result<String>>

    suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan): Flow<Result<Unit>>
}