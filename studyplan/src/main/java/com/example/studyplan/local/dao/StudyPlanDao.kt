package com.example.studyplan.local.dao

import androidx.room.*
import com.example.studyplan.local.entity.StudyPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StudyPlanDao {

    companion object { const val FAILED = -1L }

    @Query("SELECT * FROM studyplanentity")
    abstract fun observeStudyPlans(): Flow<List<StudyPlanEntity>>

    @Query("SELECT * FROM studyplanentity")
    abstract fun studyPlans(): List<StudyPlanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertElseUpdateStudyPlan(studyPlan: StudyPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(studyPlanEntity: StudyPlanEntity)

    @Query("DELETE FROM studyplanentity WHERE studyPlanId = :id")
    abstract suspend fun delete(id: String)

    @Query("SELECT * FROM studyplanentity WHERE studyPlanId = :userId")
    abstract fun studyPlan(userId: String): List<StudyPlanEntity?>

    @Query("SELECT * FROM studyplanentity WHERE studyPlanId = :userId")
    abstract fun studyPlanFlow(userId: String): Flow<StudyPlanEntity?>

    @Query("UPDATE studyplanentity SET isFavorite = :isFavorite where studyPlanId = :studyPlanId")
    abstract fun updateStudyPlanFavorite(studyPlanId: String, isFavorite: Boolean)

    @Query("DELETE FROM studyplanentity")
    abstract suspend fun deleteAll()

}