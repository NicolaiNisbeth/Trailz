package com.dtu.base.domain

data class Semester(
    val order: Int = -1,
    val courses: List<Course> = emptyList()
)