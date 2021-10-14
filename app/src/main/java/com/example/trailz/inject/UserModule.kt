package com.example.trailz.inject

import com.example.user.UserRepository
import com.example.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class UserModule {

    @Provides
    @ViewModelScoped
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }
}
