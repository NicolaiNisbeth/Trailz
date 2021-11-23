package com.example.trailz

import androidx.compose.runtime.mutableStateMapOf
import com.example.base.domain.Course
import com.example.trailz.ui.studyplan.StudyPlanUtil
import org.junit.Test
import org.junit.Assert.*

class MyStudyPlanViewModelTest {

    @Test
    fun collapsed_to_expanded(){
        // GIVEN
        val semesterId = 1
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = mutableStateMapOf(semesterId to listOf(Course("01234 BasisMat"))),
            collapsedSemesters = mutableStateMapOf(semesterId to true)
        )

        // WHEN
        studyPlanUtil.expandSemester(semesterId)

        // THEN
        assertEquals(false, studyPlanUtil.collapsedSemesters[semesterId])
    }

    @Test
    fun expanded_to_collapsed(){
        // GIVEN
        val semesterId = 1
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = mutableStateMapOf(semesterId to listOf(Course("01234 BasisMat"))),
            collapsedSemesters = mutableStateMapOf(semesterId to true)
        )

        // WHEN
        studyPlanUtil.expandSemester(semesterId)

        // THEN
        assertEquals(false, studyPlanUtil.collapsedSemesters[semesterId])
    }

    @Test
    fun all_collapsed_to_all_expanded(){
        // GIVEN
        val semesterId = 1
        val semesterId2 = 2
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = mutableStateMapOf(
                semesterId to listOf(Course("01234 BasisMat")),
                semesterId2 to listOf(Course("43210 BasisMat2"))
            ),
            collapsedSemesters = mutableStateMapOf(
                semesterId to true,
                semesterId2 to true
            )
        )

        // WHEN
        studyPlanUtil.flipAllSemesterVisibility(allCollapsed = true)

        // THEN
        assertEquals(false, studyPlanUtil.collapsedSemesters[semesterId])
        assertEquals(false, studyPlanUtil.collapsedSemesters[semesterId2])
    }

    @Test
    fun all_expanded_to_all_collapsed(){
        // GIVEN
        val semesterId = 1
        val semesterId2 = 2
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = mutableStateMapOf(
                semesterId to listOf(Course("01234 BasisMat")),
                semesterId2 to listOf(Course("43210 BasisMat2"))
            ),
            collapsedSemesters = mutableStateMapOf(
                semesterId to false,
                semesterId2 to false
            )
        )

        // WHEN
        studyPlanUtil.flipAllSemesterVisibility(allCollapsed = false)

        // THEN
        assertEquals(true, studyPlanUtil.collapsedSemesters[semesterId])
        assertEquals(true, studyPlanUtil.collapsedSemesters[semesterId2])
    }

    @Test
    fun added_semester_is_expanded(){
        // GIVEN
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = mutableStateMapOf(),
            collapsedSemesters = mutableStateMapOf()
        )

        // WHEN
        val semesterId = 1
        val courses = listOf(Course("01234 BasisMat"))
        studyPlanUtil.addSemester(semesterId, courses)

        // THEN
        assertEquals(false, studyPlanUtil.collapsedSemesters[semesterId])
    }

    @Test
    fun added_semester_is_highest_key(){
        // GIVEN
        val semesterId = 1
        val courses = listOf(Course("01234 BasisMat"))
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = mutableStateMapOf(semesterId to courses),
            collapsedSemesters = mutableStateMapOf(semesterId to false)
        )
        assertEquals(semesterId, studyPlanUtil.collapsedSemesters.keys.maxOrNull())

        // WHEN
        studyPlanUtil.addSemester()

        // THEN
        assertEquals(2, studyPlanUtil.collapsedSemesters.keys.maxOrNull())
    }

    @Test
    fun new_semester_title_ignored_when_exists_already(){
        // GIVEN
        val semesterId = 1
        val courses = listOf(Course("01234 BasisMat"))
        val semesterId2 = 2
        val courses2 = listOf(Course("43210 BasisMat2"))
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = mutableStateMapOf(semesterId to courses, semesterId2 to courses2),
            collapsedSemesters = mutableStateMapOf(semesterId to false, semesterId2 to false)
        )

        // WHEN
        studyPlanUtil.editSemester(semesterId2, newTitle = "1")

        // THEN
        assertTrue(studyPlanUtil.semesterToCourses.containsKey(semesterId))
        assertTrue(studyPlanUtil.semesterToCourses.containsKey(semesterId2))
    }

    @Test
    fun add_course_is_ignored_when_no_title(){
        // GIVEN
        val semesterId = 1
        val courses = listOf(Course("01234 BasisMat"))
        val semesterToCourses = mutableStateMapOf(semesterId to courses)
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = semesterToCourses,
            collapsedSemesters = mutableStateMapOf(semesterId to false)
        )

        // WHEN
        studyPlanUtil.addCourse(Course(""), 2)

        // THEN
        assertEquals(semesterToCourses, studyPlanUtil.semesterToCourses)
    }

    @Test
    fun replace_course_is_ignored_when_new_course_no_title(){
        // GIVEN
        val semesterId = 1
        val courses = listOf(Course("01234 BasisMat"))
        val semesterToCourses = mutableStateMapOf(semesterId to courses)
        val studyPlanUtil = StudyPlanUtil(
            semesterToCourses = semesterToCourses,
            collapsedSemesters = mutableStateMapOf(semesterId to false)
        )

        // WHEN
        studyPlanUtil.replaceCourseAt(0, 1, Course(""))

        // THEN
        assertEquals(semesterToCourses, studyPlanUtil.semesterToCourses)
    }
}