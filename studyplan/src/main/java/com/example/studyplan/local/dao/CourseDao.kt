package com.example.studyplan.local.dao

import androidx.room.*
import com.example.base.domain.Course
import com.example.studyplan.local.entity.CourseEntity
import com.example.studyplan.local.entity.StudyPlanEntity
import com.example.studyplan.local.entity.StudyPlanWithSemestersAndCourses
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CourseDao {

    companion object { const val FAILED = -1L }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertElseIgnoreCourse(course: CourseEntity): Long

    @Query("DELETE FROM courseentity")
    abstract suspend fun deleteAll()
}