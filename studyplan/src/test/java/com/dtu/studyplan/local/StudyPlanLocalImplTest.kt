package com.dtu.studyplan.local

import com.dtu.studyplan.local.dao.StudyPlanDao
import com.dtu.studyplan.local.entity.StudyPlanEntity
import com.dtu.studyplan.local.mapper.toDomain
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StudyPlanLocalImplTest {

    lateinit var localeDataSource: StudyPlanLocalImpl

    @Mock
    lateinit var studyPlanDao: StudyPlanDao

    @Before
    fun setup() {
        localeDataSource = StudyPlanLocalImpl(
            studyPlanDao = studyPlanDao
        )
    }

    @Test
    fun `get study plan with id when exists`() = runBlocking {
        // GIVEN
        val id = "1"
        val studyPlans = listOf(StudyPlanEntity(id, "title", "username", "updated", 0, emptyList(), false))
        `when`(studyPlanDao.studyPlan(id)).thenReturn(studyPlans)

        // WHEN
        val studyPlan = localeDataSource.getStudyPlan(id)

        // THEN
        assertEquals(studyPlans.first().toDomain(), studyPlan)
    }

    @Test
    fun `get null when study plan with id does not exists`() = runBlocking {
        // GIVEN
        val unknownId = "2"

        // WHEN
        val studyPlan = localeDataSource.getStudyPlan(unknownId)

        // THEN
        assertNull(studyPlan)
    }

}