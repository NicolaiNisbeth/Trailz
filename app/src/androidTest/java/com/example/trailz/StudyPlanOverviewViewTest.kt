package com.example.trailz

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.base.domain.Course
import com.example.base.domain.Semester
import com.example.trailz.ui.common.studyplan.StudyPlanOverView
import com.google.android.material.composethemeadapter.MdcTheme
import org.junit.Rule
import org.junit.Test

class StudyPlanOverviewViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @ExperimentalAnimationApi
    @Test
    fun semester_invisible_to_visible_on_expanded() {
        // GIVEN
        var isExpanded by mutableStateOf(false)
        composeTestRule.setContent {
            MdcTheme {
                StudyPlanOverView(
                    id = "1",
                    username = "nicolai",
                    title = "My favorite studyplan",
                    likes = 1,
                    lastUpdated = "21/11/2021",
                    isChecked = false,
                    isExpanded = isExpanded,
                    semesters = listOf(Semester(1, listOf(Course("012345 Basismat")))),
                    onExpandClicked = { isExpanded = !isExpanded },
                    onUpdateFavorite = {id, isFavorite, likes ->},
                    onStudyPlan = {id -> }
                )
            }
        }
        composeTestRule.onNodeWithText("012345 Basismat").assertDoesNotExist()

        // WHEN
        composeTestRule.onNodeWithTag("expandButton").performClick()

        // THEN
        composeTestRule.onNodeWithText("012345 Basismat").assertExists()
    }

    @ExperimentalAnimationApi
    @Test
    fun semester_visible_to_invisible_on_collapsed() {
        // GIVEN
        var isExpanded by mutableStateOf(true)
        composeTestRule.setContent {
            MdcTheme {
                StudyPlanOverView(
                    id = "1",
                    username = "nicolai",
                    title = "My favorite studyplan",
                    likes = 1,
                    lastUpdated = "21/11/2021",
                    isChecked = false,
                    isExpanded = isExpanded,
                    semesters = listOf(Semester(1, listOf(Course("012345 Basismat")))),
                    onExpandClicked = { isExpanded = !isExpanded },
                    onUpdateFavorite = {id, isFavorite, likes ->},
                    onStudyPlan = {id -> }
                )
            }
        }
        composeTestRule.onNodeWithText("012345 Basismat").assertExists()

        // WHEN
        composeTestRule.onNodeWithTag("expandButton").performClick()

        // THEN
        composeTestRule.onNodeWithText("012345 Basismat").assertDoesNotExist()
    }
}