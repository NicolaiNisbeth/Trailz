package com.example.trailz.inject

import android.content.Context
import com.example.trailz.ui.signup.BecomeUserUseCase
import com.example.trailz.ui.signup.CreateUserUseCase
import com.example.trailz.ui.signup.DeleteUserUseCase
import com.example.trailz.ui.signup.GetUserUseCase
import com.example.trailz.ui.signup.UserFirebase
import com.example.trailz.ui.signup.UserRepository
import com.example.trailz.ui.signup.UserRepositoryImpl
import com.example.trailz.ui.signup.UserService
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule{
    @Provides
    @Singleton
    fun provideDatabaseReference(): FirebaseDatabase {
        return FirebaseDatabase
            .getInstance("https://trailz-4000f-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext appContext: Context): SharedPrefs {
        return SharedPrefs(appContext)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
class UserModule {

    @Provides
    @ViewModelScoped
    fun provideCreateUserUseCase(repository: UserRepository): CreateUserUseCase {
        return CreateUserUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetUserUseCase(repository: UserRepository): GetUserUseCase {
        return GetUserUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteUserUseCase(repository: UserRepository): DeleteUserUseCase {
        return DeleteUserUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideBecomeUserUseCase(repository: UserRepository): BecomeUserUseCase {
        return BecomeUserUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun providesUserRepository(userService: UserService): UserRepository {
        return UserRepositoryImpl(userService)
    }

    @Provides
    @ViewModelScoped
    fun provideUserService(database: FirebaseDatabase): UserService {
        return UserFirebase(database)
    }
}