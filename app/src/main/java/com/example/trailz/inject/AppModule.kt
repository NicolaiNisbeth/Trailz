package com.example.trailz.inject

import android.content.Context
import androidx.room.Room
import com.example.studyplan.CacheUtil
import com.example.studyplan.local.AppDataBase
import com.example.studyplan.local.entity.Converters
import com.example.studyplan.local.entity.GsonParser
import com.example.trailz.TrailzApplication
import com.example.trailz.cache.CacheHelper
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
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

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext appContext: Context): AppDataBase {
        return Room.databaseBuilder(appContext, AppDataBase::class.java, "data.db")
            .fallbackToDestructiveMigration()
            .addTypeConverter(Converters(GsonParser(Gson())))
            .build()
    }

    @Provides
    @Singleton
    fun provideCacheUtil(@ApplicationContext appContext: Context): CacheUtil {
        return CacheHelper(appContext)
    }

    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}