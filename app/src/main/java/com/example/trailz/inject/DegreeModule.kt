package com.example.trailz.inject

import com.example.degree.DegreeRepository
import com.example.degree.DegreeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class DegreeModule {
    @Provides
    @ViewModelScoped
    fun provideDegreeRepository(): DegreeRepository {
        return DegreeRepositoryImpl()
    }
}