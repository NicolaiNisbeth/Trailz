package com.example.trailz.ui.mystudyplan

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.trailz.ui.common.Event
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

    private val _isUpdated = MutableLiveData<Event<Boolean>>()
    val isUpdated: LiveData<Event<Boolean>> = _isUpdated

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var collapsedSemesters = mutableStateMapOf<Int, Boolean>()
    var semesterToCourses = mutableStateMapOf<Int, List<Course>>()
    var inEditMode = mutableStateOf(false)

    init {
        viewModelScope.launch {
            repository.getStudyPlan(sharedPrefs.loggedInId!!).collect {
                when(it){
                    is Result.Failed -> { inEditMode.value = true; _isLoading.value = false }
                    is Result.Loading -> _isLoading.value = false
                    is Result.Success -> {
                        val studyPlan = it.data
                        studyPlan.semesters.forEach { addSemester(it.order, it.courses) }
                        studyPlan.semesters.forEach { expandSemester(it.order) }
                        _savedStudyPlan.value = studyPlan
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    private fun isStudyPlanModified(unsavedStudyPlan: StudyPlan) = unsavedStudyPlan != _savedStudyPlan.value

    fun changeEditMode(edit: Boolean){
        inEditMode.value = edit
    }

    fun isSemesterCollapsed(semesterId: Int) = collapsedSemesters[semesterId] ?: false

    fun collapseSemester(semesterId: Int) { collapsedSemesters[semesterId] = true }

    fun expandSemester(semesterID: Int) { collapsedSemesters[semesterID] = false }

    fun isAnyCollapsed() = collapsedSemesters.any { it.value }

    fun toggleAllSemesters(allCollapsed: Boolean){
        semesterToCourses.keys.forEach { if (allCollapsed) expandSemester(it) else collapseSemester(it) }
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

    fun editSemester(id: Int, newTitle: String){
        val title = newTitle.toIntOrNull() ?: return
        val isTitleUnique = semesterToCourses[title] == null
        if (isTitleUnique){
            semesterToCourses[title] = semesterToCourses[id] ?: emptyList()
            removeSemester(id)
        }
    }

    fun addCourse(course: Course, semesterId: Int) {
        if (course.title.isNotBlank()){
            semesterToCourses[semesterId] = semesterToCourses[semesterId]
                ?.plus(course)
                ?: emptyList()
        }
    }

    fun removeCourse(course: Course, semesterId: Int) {
        semesterToCourses[semesterId] = semesterToCourses[semesterId]
            ?.minus(course)
            ?: emptyList()
    }

    fun replaceCourseAt(index: Int, semesterId: Int, course: Course){
        if (course.title.isNotBlank()){
            semesterToCourses[semesterId]?.toMutableList()?.let {
                it[index] = course
                semesterToCourses[semesterId] = it
            }
        }
    }

    fun saveStudyPlan(){
        val userId = sharedPrefs.loggedInId ?: "-1"
        val studyPlan = StudyPlan(
            userId = userId,
            title = userId,
            semesters = semesterToCourses.map { Semester(it.key, it.value) }
        )
        if (isStudyPlanModified(studyPlan)){
            viewModelScope.launch {
                repository.createStudyPlan(studyPlan).collect {
                    when (it){
                        is Result.Failed -> { _isLoading.value = false; _isUpdated.value = Event(false) }
                        is Result.Loading -> _isLoading.value = true
                        is Result.Success -> {
                            _isLoading.value = false
                            _isUpdated.value = Event(true)
                        }
                    }
                }
            }
        }
    }
}
