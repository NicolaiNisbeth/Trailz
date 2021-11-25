package com.dtu.trailz.inject

import com.dtu.favorite.FavoriteRepository
import com.dtu.favorite.FavoriteRepositoryImpl
import com.dtu.studyplan.StudyPlanRepository
import com.dtu.trailz.ui.favorites.usecase.GetFavoritesUseCase
import com.dtu.trailz.ui.favorites.usecase.UpdateFavoriteUseCase
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

    @Provides
    @ViewModelScoped
    fun provideGetFavoriteUseCase(
        studyPlanRepository: StudyPlanRepository,
        favoriteRepository: FavoriteRepository
    ): GetFavoritesUseCase {
        return GetFavoritesUseCase(studyPlanRepository, favoriteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateFavoriteUseCase(
        studyPlanRepository: StudyPlanRepository,
        favoriteRepository: FavoriteRepository
    ): UpdateFavoriteUseCase {
        return UpdateFavoriteUseCase(favoriteRepository, studyPlanRepository)
    }

}