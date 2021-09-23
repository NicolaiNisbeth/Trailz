package com.example.trailz.inject

import com.example.trailz.ui.marketplace.*
import com.example.trailz.ui.signup.*
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class StudyPlanerModule {

    @Provides
    @ViewModelScoped
    fun providegetAllStudyPlansUseCase(repository: StudyPLansRepository): GetAllStudyPlansUseCase {
        return GetAllStudyPlansUseCase(repository)
    }
    @Provides
    @ViewModelScoped
    fun providesStudyPlanRepositoryImpl(studyplansService: StudyPlanService): StudyPLansRepository {
        return StudyPlanRepositoryImpl(studyplansService)
    }

    @Provides
    @ViewModelScoped
    fun provideUserService(database: FirebaseDatabase): StudyPlanService {
        return StudyPlanFirebase(database)
    }
}