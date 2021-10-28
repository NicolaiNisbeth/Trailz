package com.example.studyplan.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studyplan.local.dao.CourseDao
import com.example.studyplan.local.dao.SemesterDao
import com.example.studyplan.local.dao.StudyPlanDao
import com.example.studyplan.local.entity.CourseEntity
import com.example.studyplan.local.entity.SemesterCourseCrossRef
import com.example.studyplan.local.entity.SemesterEntity
import com.example.studyplan.local.entity.StudyPlanEntity

@Database(
    entities = [
        StudyPlanEntity::class, SemesterEntity::class, CourseEntity::class, SemesterCourseCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase: RoomDatabase() {
    abstract fun studyPlanDao(): StudyPlanDao
    abstract fun courseDao(): CourseDao
    abstract fun semesterDao(): SemesterDao
}