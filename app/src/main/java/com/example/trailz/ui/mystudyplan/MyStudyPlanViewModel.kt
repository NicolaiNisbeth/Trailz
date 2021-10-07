package com.example.trailz.ui.mystudyplan

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.Result
import com.example.base.domain.Course
import com.example.base.domain.Semester
import com.example.base.domain.StudyPlan
import com.example.studyplan.StudyPlanRepository
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyStudyPlanViewModel @Inject constructor(
    private val repository: StudyPlanRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _savedStudyPlan = MutableLiveData<StudyPlan>()

    private val _unsavedStudyPLan = MutableLiveData<StudyPlan>()
    val unsavedStudyPlan: LiveData<StudyPlan> = _unsavedStudyPLan

    private val _isUpdated = MutableLiveData<Boolean>()
    val isUpdated: LiveData<Boolean> = _isUpdated

    private var collapsedSemesters = mutableStateMapOf<Int, Boolean>()

    var semesterToCourses = mutableStateMapOf<Int, List<Course>>()

    var inEditMode = mutableStateOf(false)

    private fun isStudyPlanModified() = unsavedStudyPlan.value != _savedStudyPlan.value

    fun changeEditMode(edit: Boolean){ inEditMode.value = edit }

    fun isSemesterCollapsed(semesterId: Int) = collapsedSemesters[semesterId] ?: false

    fun collapseSemester(semesterId: Int) { collapsedSemesters[semesterId] = true }

    fun expandSemester(semesterID: Int) { collapsedSemesters[semesterID] = false }

    fun addSemester() {
        val newSemester = semesterToCourses.count()
        semesterToCourses[newSemester] = emptyList()
        collapsedSemesters[newSemester] = false
    }

    fun removeSemester(semesterId: Int) {
        semesterToCourses.remove(semesterId)
        collapsedSemesters.remove(semesterId)
    }

    fun addCourse(course: Course, semesterId: Int) {
        semesterToCourses[semesterId] = semesterToCourses[semesterId]
            ?.plus(course)
            ?: emptyList()
    }

    fun removeCourse(course: Course, semesterId: Int) {
        semesterToCourses[semesterId] = semesterToCourses[semesterId]
            ?.minus(course)
            ?: emptyList()
    }

    fun replaceCourseAt(index: Int, semesterId: Int, course: Course){
        semesterToCourses[semesterId]?.toMutableList()?.let {
            it[index] = course
            semesterToCourses[semesterId] = it
        }
    }

    fun saveStudyPlan(studyPlan: StudyPlan){
        if (!isStudyPlanModified()) return

        viewModelScope.launch {
            repository.createStudyPlan(studyPlan).collect {
                when (it){
                    is Result.Failed -> { _isUpdated.value = true }
                    is Result.Loading -> {}
                    is Result.Success -> { _isUpdated.value = true }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            val studyPlan = StudyPlan(
                userId = sharedPrefs.loggedInId ?: "userId",
                title = "Awesome study planner",
                semesters = listOf(
                    Semester(order = 1, emptyList()),
                    Semester(order = 2, emptyList()),
                    Semester(order = 3, emptyList()),
                    Semester(order = 4, emptyList()),
                )
            )
            _savedStudyPlan.value = studyPlan
            _unsavedStudyPLan.value = studyPlan
            semesterToCourses = studyPlan.semesters.map { it.order to it.courses }.toMutableStateMap()
            collapsedSemesters = studyPlan.semesters
                .map { it.order to false }
                .toMutableStateMap()
            /*
            repository.getStudyPlan(sharedPrefs.loggedInId).collect {
                when(it){
                    is Result.Failed -> { }
                    is Result.Loading -> {}
                    is Result.Success -> {
                        val studyPlan = it.data
                        _savedStudyPlan.value = studyPlan
                        _unsavedStudyPLan.value = studyPlan
                        collapsedSemesters = studyPlan.semesters
                            .map { it.order to false }
                            .toMutableStateMap()
                    }
                }
            }

             */
        }
    }

}