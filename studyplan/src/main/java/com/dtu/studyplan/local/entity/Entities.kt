package com.dtu.studyplan.local.entity

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

@Entity
data class StudyPlanEntity(
    @PrimaryKey val studyPlanId: String,
    val title: String,
    val username: String,
    val updated: String,
    val likes: Long,
    val semesters: List<SemesterEntity>,
    val isFavorite: Boolean
)

@Entity
data class SemesterEntity(
    val order: Int,
    val courses: List<CourseEntity>
){
    @PrimaryKey(autoGenerate = true) var semesterId = 0L
}

@Entity
data class CourseEntity(
    @PrimaryKey val title: String
)

interface JsonParser {
    fun <T> fromJson(json: String, type: Type): T?
    fun <T> toJson(obj: T, type: Type): String?
}

class GsonParser(
    private val gson: Gson
): JsonParser {
    override fun <T> fromJson(json: String, type: Type): T? {
        return gson.fromJson(json, type)
    }

    override fun <T> toJson(obj: T, type: Type): String? {
        return gson.toJson(obj, type)
    }
}

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
){
    @TypeConverter
    fun fromSemestersJson(json: String): List<SemesterEntity> {
        return jsonParser.fromJson<List<SemesterEntity>>(
            json,
            object: TypeToken<List<SemesterEntity>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toSemestersJson(semesters: List<SemesterEntity>): String{
        return jsonParser.toJson(
            semesters,
            object: TypeToken<List<SemesterEntity>>(){}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromCoursesJson(json: String): List<CourseEntity> {
        return jsonParser.fromJson<List<CourseEntity>>(
            json,
            object: TypeToken<List<CourseEntity>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toCoursesJson(semesters: List<CourseEntity>): String{
        return jsonParser.toJson(
            semesters,
            object: TypeToken<List<CourseEntity>>(){}.type
        ) ?: "[]"
    }
}
