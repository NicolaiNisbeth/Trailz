package com.example.trailz.ui.studyplan

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.base.domain.Course

class StudyPlanUtil(
    val semesterToCourses: SnapshotStateMap<Int, List<Course>>,
    val collapsedSemesters: SnapshotStateMap<Int, Boolean> = mutableStateMapOf()
){

    fun isSemesterCollapsed(semesterId: Int) = collapsedSemesters[semesterId] ?: false

    fun collapseSemester(semesterId: Int) {
        collapsedSemesters[semesterId] = true
    }

    fun expandSemester(semesterID: Int) {
        collapsedSemesters[semesterID] = false
    }

    fun isAnyCollapsed() = collapsedSemesters.any { it.value }

    fun flipAllSemesterVisibility(allCollapsed: Boolean) {
        semesterToCourses.keys.forEach {
            if (allCollapsed) expandSemester(it) else collapseSemester(it)
        }
    }

    fun addSemester(semesterId: Int? = null, courses: List<Course> = emptyList()) {
        val newSemesterId = semesterId ?: semesterToCourses.keys.maxOrNull()?.plus(1) ?: 1
        semesterToCourses[newSemesterId] = courses
        collapsedSemesters[newSemesterId] = false
    }

    fun removeSemester(semesterId: Int) {
        semesterToCourses.remove(semesterId)
        collapsedSemesters.remove(semesterId)
    }

    fun editSemester(id: Int, newTitle: String) {
        val title = newTitle.toIntOrNull() ?: return
        val isTitleUnique = semesterToCourses[title] == null
        if (isTitleUnique) {
            semesterToCourses[title] = semesterToCourses[id] ?: emptyList()
            removeSemester(id)
        }
    }

    fun addCourse(course: Course, semesterId: Int) {
        if (course.title.isNotBlank()) {
            semesterToCourses[semesterId] = semesterToCourses[semesterId]
                ?.plus(course.copy(title = course.title.trim()))
                ?: emptyList()
        }
    }

    fun removeCourse(course: Course, semesterId: Int) {
        semesterToCourses[semesterId] = semesterToCourses[semesterId]
            ?.minus(course)
            ?: emptyList()
    }

    fun replaceCourseAt(index: Int, semesterId: Int, course: Course) {
        if (course.title.isNotBlank()) {
            semesterToCourses[semesterId]?.toMutableList()?.let {
                it[index] = course.copy(title = course.title.trim())
                semesterToCourses[semesterId] = it
            }
        }
    }

}