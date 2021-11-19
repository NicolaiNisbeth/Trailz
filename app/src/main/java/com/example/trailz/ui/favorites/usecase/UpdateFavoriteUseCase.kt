package com.example.trailz.ui.favorites.usecase

import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class UpdateFavoriteUseCase(
    private val favoriteRepository: FavoriteRepository,
    private val studyPlanRepository: StudyPlanRepository
) {
    suspend operator fun invoke(
        favoriteId: String,
        userId: String,
        isFavorite: Boolean,
        likes: Long
    ) = withContext(Dispatchers.Default){
        // responsible for handling the likes on a studyPlan
        studyPlanRepository.updateStudyPlanFavorite(favoriteId, isFavorite, likes).collect()

        // responsible for associating userId with favoriteId
        if (isFavorite) favoriteRepository.addToFavorite(favoriteId, userId)
        else favoriteRepository.removeFromFavorite(favoriteId, userId)
    }
}