package com.dtu.studyplan

import com.dtu.base.Result
import com.dtu.base.domain.StudyPlan
import com.dtu.studyplan.local.StudyPlanLocalDataSource
import com.dtu.studyplan.remote.StudyPlanRemoteDataSource
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.junit.Assert.*
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner::class)
class StudyPlanRepositoryImplTest {

    lateinit var repository: StudyPlanRepositoryImpl

    @Mock
    lateinit var localDataSource: StudyPlanLocalDataSource

    @Mock
    lateinit var remoteDataSource: StudyPlanRemoteDataSource

    @Mock
    lateinit var cacheUtil: CacheUtil

    @Before
    fun setup() {
        repository = StudyPlanRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            cacheUtil = cacheUtil
        )
    }

    @Test
    fun `get cached study plan when study plan is not stale`() = runBlocking {
        // GIVEN
        val userId = "1"
        val cachedStudyPlan = StudyPlan(userId, isFavorite = true)
        `when`(localDataSource.getStudyPlan(userId)).thenReturn(cachedStudyPlan)
        `when`(cacheUtil.isDataStale("study_plan_cache_key")).thenReturn(false)

        // WHEN
        val result = mutableListOf<Result<StudyPlan>>()
        repository.getStudyPlan(userId).toCollection(result)
        val actualStudyPlan = result.first()

        // THEN
        assertEquals(Result.success(cachedStudyPlan), actualStudyPlan)
    }


    @Test
    fun `get remote study plan when study plan stale`() = runBlocking {
        // GIVEN
        val userId = "1"
        val remoteStudyPlan = Result.success(StudyPlan(userId, isFavorite = false))
        `when`(remoteDataSource.getStudyPlan(userId)).thenReturn(remoteStudyPlan)
        `when`(cacheUtil.isDataStale("study_plan_cache_key")).thenReturn(true)

        // WHEN
        val result = mutableListOf<Result<StudyPlan>>()
        repository.getStudyPlan(userId).toCollection(result)
        val actualStudyPlan = result.first()

        // THEN
        assertEquals(remoteStudyPlan, actualStudyPlan)
    }
}