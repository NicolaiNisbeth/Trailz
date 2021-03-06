package com.dtu.studyplan.local

import com.dtu.base.domain.StudyPlan
import kotlinx.coroutines.flow.Flow

interface StudyPlanLocalDataSource {
    suspend fun getStudyPlan(id: String): StudyPlan?

    suspend fun observeStudyPlan(id: String): Flow<StudyPlan?>

    suspend fun getStudyPlans(): List<StudyPlan>

    suspend fun observeStudyPlans(): Flow<List<StudyPlan>>

    suspend fun deleteStudyPlan(id: String): String

    suspend fun createStudyPlan(studyPlan: StudyPlan): String

    suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan)

    suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean, likes: Long)

    suspend fun createStudyPlans(data: List<StudyPlan>)
}