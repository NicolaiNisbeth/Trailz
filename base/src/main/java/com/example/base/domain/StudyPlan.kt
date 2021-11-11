package com.example.base.domain

data class StudyPlan(
    val userId: String = "",
    val title: String = "",
    val updated: String = "",
    val semesters: List<Semester> = emptyList(),
    val isChecked: Boolean = false
)