package com.example.trailz.inject

import com.example.studyplan.CacheUtil
import com.example.studyplan.StudyPlanRepository
import com.example.studyplan.StudyPlanRepositoryImpl
import com.example.studyplan.local.AppDataBase
import com.example.studyplan.local.StudyPlanLocalDataSource
import com.example.studyplan.local.StudyPlanLocalImpl
import com.example.studyplan.local.dao.StudyPlanDao
import com.example.studyplan.remote.StudyPlanRemoteDataSource
import com.example.studyplan.remote.StudyPlanRemoteImpl
import com.google.firebase.firestore.FirebaseFirestore
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
        remoteDataSource: StudyPlanRemoteDataSource,
        cacheUtil: CacheUtil
    ): StudyPlanRepository {
        return StudyPlanRepositoryImpl(localDataSource, remoteDataSource, cacheUtil)
    }

    @Provides
    @ViewModelScoped
    fun provideLocalDataSource(
        studyPlanDao: StudyPlanDao,
    ): StudyPlanLocalDataSource {
        return StudyPlanLocalImpl(studyPlanDao)
    }

    @Provides
    @ViewModelScoped
    fun provideRemoteDataSource(
        firebaseFireStore: FirebaseFirestore
    ): StudyPlanRemoteDataSource {
        return StudyPlanRemoteImpl(firebaseFireStore)
    }

    @Provides
    @ViewModelScoped
    fun provideStudyPlanDao(database: AppDataBase): StudyPlanDao {
        return database.studyPlanDao()
    }
}

