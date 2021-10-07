package com.example.trailz.inject

import com.example.course.CourseRepository
import com.example.course.CourseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class CourseModule {
    @Provides
    @ViewModelScoped
    fun provideCourseRepository(): CourseRepository {
        return CourseRepositoryImpl()
    }
}