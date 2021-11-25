package com.dtu.studyplan

import com.dtu.base.domain.StudyPlan
import com.dtu.base.Result
import kotlinx.coroutines.flow.Flow

interface StudyPlanRepository {

    suspend fun observeStudyPlan(id: String): Flow<Result<StudyPlan>>

    suspend fun getStudyPlan(id: String): Flow<Result<StudyPlan>>

    suspend fun getStudyPlans(): Flow<Result<List<StudyPlan>>>

    suspend fun createMyStudyPlan(studyPlan: StudyPlan): Flow<Result<Unit>>

    suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean, likes: Long): Flow<Unit>

    suspend fun refreshStudyPlansIfStale(isForced: Boolean = false): Flow<Result<List<StudyPlan>>>
}