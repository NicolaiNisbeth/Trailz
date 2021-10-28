package com.example.studyplan.local.mapper

import com.example.base.domain.Course
import com.example.base.domain.Semester
import com.example.base.domain.StudyPlan
import com.example.studyplan.local.entity.SemesterEntity
import com.example.studyplan.local.entity.StudyPlanEntity
import com.example.studyplan.local.entity.StudyPlanWithSemestersAndCourses

fun StudyPlanWithSemestersAndCourses.toStudyPlan(): StudyPlan {
    val semesterList = mutableListOf<Semester>()
    for ((semester, courses) in semesters) {
        semesterList += Semester(
            order = semester.semesterName,
            courses = courses.map { Course(title = it.courseName) }
        )
    }
    return StudyPlan(
        userId = studyPlan.studyPlanId,
        title = studyPlan.title,
        semesters = semesterList
    )
}