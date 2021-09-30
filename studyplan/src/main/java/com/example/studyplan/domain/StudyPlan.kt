package com.example.studyplan.domain

data class StudyPlan(
    val userId: String = "",
    val title: String = "",
    val semesters: List<Semester> = emptyList()
)
