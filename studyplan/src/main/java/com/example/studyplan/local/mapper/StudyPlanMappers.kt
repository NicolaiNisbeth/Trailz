package com.example.studyplan.local.mapper

import com.example.base.domain.Course
import com.example.base.domain.Semester
import com.example.base.domain.StudyPlan
import com.example.studyplan.local.entity.CourseEntity
import com.example.studyplan.local.entity.SemesterEntity
import com.example.studyplan.local.entity.StudyPlanEntity

fun StudyPlanEntity.toDomain() = StudyPlan(
    userId = studyPlanId,
    username = username,
    title = title,
    updated = updated,
    likes = likes,
    isFavorite = isFavorite,
    semesters = semesters.map(SemesterEntity::toDomain)
)

fun StudyPlan.toEntity() = StudyPlanEntity(
    studyPlanId = userId,
    username = username,
    title = title,
    updated = updated,
    likes = likes,
    isFavorite = isFavorite,
    semesters = semesters.map(Semester::toEntity)
)

fun SemesterEntity.toDomain() = Semester(
    order = order,
    courses = courses.map(CourseEntity::toDomain)
)

fun Semester.toEntity() = SemesterEntity(
    order = order,
    courses = courses.map(Course::toEntity)
)

fun CourseEntity.toDomain() = Course(
    title = title
)

fun Course.toEntity() = CourseEntity(
    title = title
)