package com.example.trailz.ui.mystudyplan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MyStudyPlanViewModel @Inject constructor(
    private val repository: StudyPlanRepository,
    private val sharedPrefs: SharedPrefs,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ownerId = savedStateHandle.get<String>("ownerId")
        ?: sharedPrefs.loggedInId

    private val _savedStudyPlan = MutableLiveData<StudyPlan>()

    private val _isUpdated = MutableLiveData<Event<Boolean>>()
    val isUpdated: LiveData<Event<Boolean>> = _isUpdated

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var collapsedSemesters = mutableStateMapOf<Int, Boolean>()
    var semesterToCourses = mutableStateMapOf<Int, List<Course>>()
    var header = mutableStateOf(Triple(ownerId, "", ""))
    var inEditMode = mutableStateOf(false)

    init {
        viewModelScope.launch {
            repository.getStudyPlan(ownerId).collect {
                when (it) {
                    is Result.Loading -> _isLoading.value = false
                    is Result.Failed -> {
                        inEditMode.value = true
                        _isLoading.value = false
                        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        header.value = Triple(ownerId, "Click me!", date)
                    }
                    is Result.Success -> {
                        val studyPlan = it.data
                        studyPlan.semesters.forEach { addSemester(it.order, it.courses) }
                        studyPlan.semesters.forEach { expandSemester(it.order) }
                        _savedStudyPlan.value = studyPlan
                        _isLoading.value = false
                        header.value = Triple(ownerId, studyPlan.title, studyPlan.updated)
                    }
                }
            }
        }
    }

    private fun isStudyPlanModified(unsavedStudyPlan: StudyPlan) =
        unsavedStudyPlan != _savedStudyPlan.value

    fun changeEditMode(edit: Boolean) {
        inEditMode.value = edit
    }

    fun isSemesterCollapsed(semesterId: Int) = collapsedSemesters[semesterId] ?: false

    fun collapseSemester(semesterId: Int) {
        collapsedSemesters[semesterId] = true
    }

    fun expandSemester(semesterID: Int) {
        collapsedSemesters[semesterID] = false
    }

    fun isAnyCollapsed() = collapsedSemesters.any { it.value }

    fun toggleAllSemesters(allCollapsed: Boolean) {
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
                ?.plus(course)
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
                it[index] = course
                semesterToCourses[semesterId] = it
            }
        }
    }

    fun editStudyPlanTitle(newTitle: String) {
        header.value = header.value.copy(second = newTitle)
    }

    fun saveStudyPlan() {
        val userId = sharedPrefs.loggedInId
        val studyPlan = StudyPlan(
            userId = userId,
            title = header.value.second,
            semesters = semesterToCourses.map { Semester(it.key, it.value) }
        )

        if (isStudyPlanModified(studyPlan)) {
            viewModelScope.launch {
                repository.createStudyPlan(studyPlan).collect {
                    when (it) {
                        is Result.Failed -> {
                            _isLoading.value = false
                            _isUpdated.value = Event(false)
                        }
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
