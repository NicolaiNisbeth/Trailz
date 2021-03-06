package com.dtu.studyplan.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dtu.studyplan.local.dao.StudyPlanDao
import com.dtu.studyplan.local.entity.*

@Database(
    version = 2,
    exportSchema = false,
    entities = [
        StudyPlanEntity::class,
        SemesterEntity::class,
        CourseEntity::class,
    ],
)
@TypeConverters(Converters::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun studyPlanDao(): StudyPlanDao
}