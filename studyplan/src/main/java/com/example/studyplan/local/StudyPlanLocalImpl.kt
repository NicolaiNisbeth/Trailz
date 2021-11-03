package com.example.studyplan.local

import com.example.base.domain.StudyPlan
import com.example.studyplan.local.dao.CourseDao
import com.example.studyplan.local.dao.SemesterDao
import com.example.studyplan.local.dao.StudyPlanDao
import com.example.studyplan.local.entity.*
import com.example.studyplan.local.mapper.toStudyPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyPlanLocalImpl(
    private val studyPlanDao: StudyPlanDao,
    private val courseDao: CourseDao,
    private val semesterDao: SemesterDao
): StudyPlanLocalDataSource {

    override suspend fun getStudyPlan(id: String) = studyPlanDao.studyPlan(id)?.toStudyPlan()

    override suspend fun getStudyPlans(): List<StudyPlan> {
        return studyPlanDao.studyPlans().map(StudyPlanWithSemestersAndCourses::toStudyPlan)
    }

    override suspend fun observeStudyPlans(): Flow<List<StudyPlan>> {
        return studyPlanDao.observeStudyPlans().map {
            it.map(StudyPlanWithSemestersAndCourses::toStudyPlan)
        }
    }

    override suspend fun deleteStudyPlan(id: String): StudyPlan? {
        val studyPlan = getStudyPlan(id)
        studyPlanDao.delete(id)
        semesterDao.deleteAllWith(id)
        return studyPlan
    }

    override suspend fun createStudyPlan(studyPlan: StudyPlan): String {
        for (semester in studyPlan.semesters) {
            for (course in semester.courses) {
                courseDao.insertElseIgnoreCourse(CourseEntity(course.title))
            }
            semesterDao.insertElseUpdateSemester(SemesterEntity(studyPlan.userId, semester.order))
        }
        return studyPlanDao.insertElseUpdateStudyPlan(
            StudyPlanEntity(studyPlan.userId, studyPlan.title)
        ).toString()
    }

    override suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan){
        createStudyPlan(studyPlan)
    }

}
