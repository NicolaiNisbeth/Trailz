package com.example.studyplan

import com.example.base.domain.StudyPlan
import com.example.base.Result
import kotlinx.coroutines.flow.Flow

interface StudyPlanRepository {

    suspend fun observeStudyPlan(id: String): Flow<Result<StudyPlan>>

    suspend fun getStudyPlan(id: String): Flow<Result<StudyPlan>>

    suspend fun getStudyPlans(): Flow<Result<List<StudyPlan>>>

    suspend fun deleteStudyPlan(id: String): Flow<Result<Unit>>

    suspend fun createStudyPlan(studyPlan: StudyPlan): Flow<Result<Unit>>

    suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan): Flow<Result<Unit>>

    suspend fun updateStudyPlanFavorite(id:String, isFavorite: Boolean): Flow<Unit>
}