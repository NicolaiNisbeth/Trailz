package com.dtu.trailz.inject

import com.dtu.user.UserRepository
import com.dtu.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class UserModule {

    @Provides
    @ViewModelScoped
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }
}
