package com.example.trailz.inject

import android.content.Context
import com.example.trailz.TrailzApplication
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext app: Context): TrailzApplication{
        return app as TrailzApplication
    }
}