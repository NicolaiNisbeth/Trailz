package com.dtu.trailz.inject

import com.dtu.course.CourseRepository
import com.dtu.course.CourseRepositoryImpl
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