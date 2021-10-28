package com.example.studyplan.local.dao

import androidx.room.*
import com.example.base.domain.StudyPlan
import com.example.studyplan.local.entity.StudyPlanEntity
import com.example.studyplan.local.entity.StudyPlanWithSemestersAndCourses
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StudyPlanDao {

    companion object { const val FAILED = -1L }

    @Transaction
    @Query("SELECT * FROM studyplanentity")
    abstract fun observeStudyPlans(): Flow<List<StudyPlanWithSemestersAndCourses>>

    @Transaction
    @Query("SELECT * FROM studyplanentity")
    abstract fun studyPlans(): List<StudyPlanWithSemestersAndCourses>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertElseUpdateStudyPlan(studyPlan: StudyPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(studyPlanEntity: StudyPlanEntity)

    @Query("DELETE FROM studyplanentity WHERE studyPlanId = :id")
    abstract suspend fun delete(id: String)

    @Transaction
    @Query("SELECT * FROM studyplanentity WHERE studyPlanId = :userId")
    abstract fun studyPlan(userId: String): StudyPlanWithSemestersAndCourses?

}