package com.dtu.base.domain

data class StudyPlan(
    val userId: String = "",
    val username: String = "",
    val title: String = "",
    val updated: String = "",
    val likes: Long = 0,
    val semesters: List<Semester> = emptyList(),
    val isFavorite: Boolean = false
)