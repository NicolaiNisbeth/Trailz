package com.example.studyplan.local.dao

import androidx.room.*
import com.example.studyplan.local.entity.SemesterEntity


@Dao
abstract class SemesterDao {

    companion object { const val FAILED = -1L }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertElseUpdateSemester(semester: SemesterEntity): Long

    @Query("DELETE FROM semesterentity")
    abstract suspend fun deleteAll()
}