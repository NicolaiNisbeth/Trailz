package com.example.trailz.ui.favorites.usecase

import com.example.base.Result
import com.example.base.Result.*
import com.example.base.domain.StudyPlan
import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import kotlinx.coroutines.flow.toCollection

class GetFavoritesUseCase(
    private val studyPlanRepository: StudyPlanRepository,
    private val favoriteRepository: FavoriteRepository
) {

    suspend operator fun invoke(userId: String): Result<List<StudyPlan>> {
        val favorites =  when (val res = favoriteRepository.getFavoritesBy(userId)){
            is Failed -> return Result.failed(res.message)
            is Loading -> return Result.loading()
            is Success -> res.data
        }
        val studyPlansResult = mutableListOf<Result<StudyPlan>>()
        favorites.followedUserIds.forEach {
            studyPlanRepository.getStudyPlan(it).toCollection(studyPlansResult)
        }
        val studyPlans = studyPlansResult.mapNotNull {
            if (it is Success) it.data.copy(isFavorite = true)
            else null
        }
        return Result.success(studyPlans)
    }
}