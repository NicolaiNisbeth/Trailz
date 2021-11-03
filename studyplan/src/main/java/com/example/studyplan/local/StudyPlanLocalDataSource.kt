package com.example.studyplan.local

import com.example.base.Result
import com.example.base.domain.StudyPlan
import kotlinx.coroutines.flow.Flow

interface StudyPlanLocalDataSource {
    suspend fun getStudyPlan(id: String = "-1"): StudyPlan?

    suspend fun getStudyPlans(): List<StudyPlan>

    suspend fun observeStudyPlans(): Flow<List<StudyPlan>>

    suspend fun deleteStudyPlan(id: String): StudyPlan?

    suspend fun createStudyPlan(studyPlan: StudyPlan): String

    suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan)
}