package com.example.studyplan.local.dao

import androidx.room.*
import com.example.studyplan.local.entity.CourseEntity

@Dao
abstract class CourseDao {

    companion object { const val FAILED = -1L }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertElseUpdateCourse(course: CourseEntity): Long

    @Query("DELETE FROM courseentity")
    abstract suspend fun deleteAll()
}