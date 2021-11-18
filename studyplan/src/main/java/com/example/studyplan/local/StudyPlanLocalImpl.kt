package com.example.studyplan.local

import com.example.base.domain.Semester
import com.example.base.domain.StudyPlan
import com.example.studyplan.local.dao.CourseDao
import com.example.studyplan.local.dao.SemesterDao
import com.example.studyplan.local.dao.StudyPlanDao
import com.example.studyplan.local.entity.*
import com.example.studyplan.local.mapper.toDomain
import com.example.studyplan.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyPlanLocalImpl(
    private val studyPlanDao: StudyPlanDao,
    private val courseDao: CourseDao,
    private val semesterDao: SemesterDao
): StudyPlanLocalDataSource {

    override suspend fun getStudyPlan(id: String) = studyPlanDao.studyPlan(id)
        .first()?.toDomain()

    override suspend fun getStudyPlans(): List<StudyPlan> {
        return studyPlanDao.studyPlans()
            .map { it.toDomain() }
    }

    override suspend fun observeStudyPlans(): Flow<List<StudyPlan>> {
        return studyPlanDao.observeStudyPlans().map {
            it.map(StudyPlanEntity::toDomain)
        }
    }

    override suspend fun deleteStudyPlan(id: String): String {
        studyPlanDao.delete(id)
        return id
    }

    override suspend fun createStudyPlan(studyPlan: StudyPlan): String {
        studyPlanDao.insertElseUpdateStudyPlan(
            StudyPlanEntity(
                studyPlanId = studyPlan.userId,
                title = studyPlan.title,
                username = studyPlan.username,
                updated = studyPlan.updated,
                likes = studyPlan.likes,
                isFavorite = studyPlan.isFavorite,
                semesters = studyPlan.semesters.map(Semester::toEntity)
            )
        )
        return studyPlan.userId
    }

    override suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan){
        createStudyPlan(studyPlan)
    }

    override suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean) {
        getStudyPlan(id)?.let {
            val likes = if (isFavorite) it.likes.plus(1) else it.likes.minus(1)
            updateStudyPlan(id, it.copy(isFavorite = isFavorite, likes = likes))
            val studyPlan = getStudyPlan(id)
            println(studyPlan)
        }
    }

}
