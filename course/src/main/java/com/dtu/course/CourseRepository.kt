package com.dtu.course

import com.dtu.base.Result
import com.dtu.base.domain.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(): Flow<Result<List<Course>>>
    fun observeCourses(): Flow<Result<List<Course>>>
}