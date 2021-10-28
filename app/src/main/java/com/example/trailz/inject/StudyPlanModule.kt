package com.example.trailz.inject

import com.example.studyplan.StudyPlanRepository
import com.example.studyplan.StudyPlanRepositoryImpl
import com.example.studyplan.local.AppDataBase
import com.example.studyplan.local.StudyPlanLocalDataSource
import com.example.studyplan.local.StudyPlanLocalImpl
import com.example.studyplan.local.dao.CourseDao
import com.example.studyplan.local.dao.SemesterDao
import com.example.studyplan.local.dao.StudyPlanDao
import com.example.studyplan.remote.StudyPlanRemoteDataSource
import com.example.studyplan.remote.StudyPlanRemoteImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class StudyPlanModule {

    @Provides
    @ViewModelScoped
    fun provideStudyPlanRepository(
        localDataSource: StudyPlanLocalDataSource,
        remoteDataSource: StudyPlanRemoteDataSource
    ): StudyPlanRepository {
        return StudyPlanRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Provides
    @ViewModelScoped
    fun provideLocalDataSource(
        studyPlanDao: StudyPlanDao,
        courseDao: CourseDao,
        semesterDao: SemesterDao
    ): StudyPlanLocalDataSource {
        return StudyPlanLocalImpl(studyPlanDao, courseDao, semesterDao)
    }

    @Provides
    @ViewModelScoped
    fun provideRemoteDataSource(): StudyPlanRemoteDataSource {
        return StudyPlanRemoteImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideStudyPlanDao(database: AppDataBase): StudyPlanDao {
        return database.studyPlanDao()
    }

    @Provides
    @ViewModelScoped
    fun provideCourseDao(database: AppDataBase): CourseDao {
        return database.courseDao()
    }

    @Provides
    @ViewModelScoped
    fun provideSemesterDao(database: AppDataBase): SemesterDao {
        return database.semesterDao()
    }

}

