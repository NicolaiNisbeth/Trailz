package com.example.trailz.ui.favorites.usecase

import com.example.base.Result
import com.example.base.domain.Favorite
import com.example.base.domain.StudyPlan
import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetFavoritesUseCaseTest {

    private lateinit var getFavoritesUseCase: GetFavoritesUseCase

    @Mock
    lateinit var studyPlanRepository: StudyPlanRepository

    @Mock
    lateinit var favoriteRepository: FavoriteRepository

    @Before
    fun setup() {
        getFavoritesUseCase = GetFavoritesUseCase(
            studyPlanRepository = studyPlanRepository,
            favoriteRepository = favoriteRepository
        )
    }

    @Test
    fun `no favorites should return empty list`() = runBlocking {
        // GIVEN
        val userId = "1"
        val noFavorites = Favorite(userId, emptyList())
        `when`(favoriteRepository.getFavoritesBy(userId))
            .thenReturn(Result.success(noFavorites))

        // WHEN
        val result = getFavoritesUseCase.invoke(userId)

        // THEN
        result.collect {
            assertEquals(it, Result.success<List<StudyPlan>>(emptyList()))
        }
    }


    @Test
    fun `a favorite should return list with a study plan`() = runBlocking {
        // GIVEN
        val userId = "1"
        val favoriteUserId = "2"
        val noFavorites = Favorite(userId, listOf(favoriteUserId))
        `when`(favoriteRepository.getFavoritesBy(userId))
            .thenReturn(Result.success(noFavorites))
        `when`(studyPlanRepository.getStudyPlan(favoriteUserId))
            .thenReturn(
                flowOf(Result.success(StudyPlan(favoriteUserId)))
            )

        // WHEN
        val result = getFavoritesUseCase.invoke(userId)

        // THEN
        val expected = Result.success(listOf(StudyPlan(favoriteUserId, isFavorite = true)))
        result.collect {
            assertEquals(expected, it)
        }
    }

}