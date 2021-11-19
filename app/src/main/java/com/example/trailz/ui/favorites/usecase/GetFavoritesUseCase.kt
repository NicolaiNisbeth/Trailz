package com.example.trailz.ui.favorites.usecase

import com.example.base.Result
import com.example.base.Result.*
import com.example.base.domain.StudyPlan
import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.withContext

class GetFavoritesUseCase(
    private val studyPlanRepository: StudyPlanRepository,
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(userId: String) = withContext(Dispatchers.Default) {
        flow<Result<List<StudyPlan>>> {
            val favRes = favoriteRepository.getFavoritesBy(userId)
            val favorites = if (favRes is Success) favRes.data.followedUserIds else emptyList()

            val studyPlansResult = mutableListOf<Result<StudyPlan>>()
            favorites.forEach {
                studyPlanRepository.getStudyPlan(it).toCollection(studyPlansResult)
            }

            val studyPlans = studyPlansResult.mapNotNull {
                if (it is Success) it.data.copy(isFavorite = true)
                else null
            }.toSet()

            emit(Result.success(studyPlans.toList()))
        }
    }
}