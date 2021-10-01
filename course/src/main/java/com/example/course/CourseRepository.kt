package com.example.course

import com.example.base.Result
import com.example.base.domain.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(): Flow<Result<List<Course>>>
    fun observeCourses(): Flow<Result<List<Course>>>
}