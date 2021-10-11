package com.example.base.domain

data class Semester(
    val title: String = "",
    val order: Int = -1,
    val courses: List<Course> = emptyList()
)