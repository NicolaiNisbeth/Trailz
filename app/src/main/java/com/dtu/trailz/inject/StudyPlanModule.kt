package com.dtu.trailz.inject

import com.dtu.studyplan.CacheUtil
import com.dtu.studyplan.StudyPlanRepository
import com.dtu.studyplan.StudyPlanRepositoryImpl
import com.dtu.studyplan.local.AppDataBase
import com.dtu.studyplan.local.StudyPlanLocalDataSource
import com.dtu.studyplan.local.StudyPlanLocalImpl
import com.dtu.studyplan.local.dao.StudyPlanDao
import com.dtu.studyplan.remote.StudyPlanRemoteDataSource
import com.dtu.studyplan.remote.StudyPlanRemoteImpl
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

