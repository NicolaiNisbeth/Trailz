package com.example.studyplan.local.dao

import androidx.room.*
import com.example.base.domain.Semester
import com.example.studyplan.local.entity.SemesterEntity
import com.example.studyplan.local.entity.StudyPlanEntity
import com.example.studyplan.local.entity.StudyPlanWithSemestersAndCourses
import kotlinx.coroutines.flow.Flow


@Dao
abstract class SemesterDao {

    companion object { const val FAILED = -1L }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertElseUpdateSemester(semester: SemesterEntity): Long

    @Query("DELETE FROM semesterentity WHERE studyPlanCreatorId = :creatorId")
    abstract suspend fun deleteAll(creatorId: String)
}