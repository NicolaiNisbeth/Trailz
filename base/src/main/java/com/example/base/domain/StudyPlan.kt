package com.example.base.domain

data class StudyPlan(
    val userId: String = "",
    val title: String = "",
    val updated: String = "",
    val likes: Long = 0,
    val semesters: List<Semester> = emptyList(),
    val isChecked: Boolean = false
)