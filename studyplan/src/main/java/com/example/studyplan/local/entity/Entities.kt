package com.example.studyplan.local.entity

import androidx.room.*

@Entity
data class StudyPlanEntity(
    @PrimaryKey val studyPlanId: String,
    val title: String
)

@Entity
data class SemesterEntity(
    val studyPlanCreatorId: String,
    val semesterName: Int
){
    @PrimaryKey(autoGenerate = true) var semesterId = 0L
}

@Entity
data class CourseEntity(
    val courseName: String,
){
    @PrimaryKey(autoGenerate = true) var courseId = 0L
}

@Entity(primaryKeys = ["semesterId", "courseId"])
data class SemesterCourseCrossRef(
    val semesterId: Long,
    val courseId: Long
)

data class SemesterWithCourses(
    @Embedded val semester: SemesterEntity,
    @Relation(
        parentColumn = "semesterId",
        entityColumn = "courseId",
        associateBy = Junction(SemesterCourseCrossRef::class)
    )
    val courses: List<CourseEntity>
)

data class StudyPlanWithSemestersAndCourses(
    @Embedded val studyPlan: StudyPlanEntity,
    @Relation(
        entity = SemesterEntity::class,
        parentColumn = "studyPlanId",
        entityColumn = "studyPlanCreatorId"
    )
    val semesters: List<SemesterWithCourses>
)