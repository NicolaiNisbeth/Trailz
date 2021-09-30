package com.example.trailz.inject

import com.example.favorite.FavoriteRepository
import com.example.favorite.FavoriteRepositoryImpl
import com.example.studyplan.StudyPlanRepository
import com.example.studyplan.StudyPlanRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class FavoriteModule {

    @Provides
    @ViewModelScoped
    fun provideFavoriteRepository(): FavoriteRepository {
        return FavoriteRepositoryImpl()
    }

}