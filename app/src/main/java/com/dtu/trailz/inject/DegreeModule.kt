package com.dtu.trailz.inject

import com.dtu.degree.DegreeRepository
import com.dtu.degree.DegreeRepositoryImpl
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