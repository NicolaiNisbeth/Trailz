package com.dtu.studyplan.remote

import com.dtu.base.Result
import com.dtu.base.domain.StudyPlan
import kotlinx.coroutines.flow.Flow

interface StudyPlanRemoteDataSource {

    suspend fun observeStudyPlan(id: String): Flow<Result<StudyPlan>>

    suspend fun getStudyPlan(id: String): Result<StudyPlan>

    suspend fun getStudyPlans(): Result<List<StudyPlan>>

    suspend fun observeStudyPlans(): Flow<Result<List<StudyPlan>>>

    suspend fun deleteStudyPlan(id: String): Flow<Result<Unit>>

    suspend fun createStudyPlan(studyPlan: StudyPlan): Flow<Result<Unit>>

    suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan): Flow<Result<Unit>>

    suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean): Result<Unit>
}