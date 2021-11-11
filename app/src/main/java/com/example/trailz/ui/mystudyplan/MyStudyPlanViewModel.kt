package com.example.trailz.ui.mystudyplan

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.*
import com.example.base.Result
import com.example.base.domain.Course
import com.example.base.domain.Semester
import com.example.base.domain.StudyPlan
import com.example.base.domain.User
import com.example.studyplan.StudyPlanRepository
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.common.Event
import com.example.trailz.ui.studyplanners.DataState
import com.example.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class MyStudyPlanData(
    val ownerId: String,
    val username: String,
    val title: String,
    val likes: Long = 0,
    val updatedLast: String,
    val inEditMode: Boolean = false,
    val isUpdated: Event<Boolean>? = null
)

@HiltViewModel
class MyStudyPlanViewModel @Inject constructor(
    private val studyPlanRepository: StudyPlanRepository,
    private val userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ownerId = savedStateHandle.get<String>("ownerId") ?: sharedPrefs.loggedInId
    private val savedStudyPlan = MutableLiveData<StudyPlan>()
    private var collapsedSemesters = mutableStateMapOf<Int, Boolean>()
    var semesterToCourses = mutableStateMapOf<Int, List<Course>>()

    private val _state: MutableStateFlow<DataState<MyStudyPlanData>> = MutableStateFlow(
        DataState(isLoading = true)
    )
    val state: StateFlow<DataState<MyStudyPlanData>> = _state

    init {
        viewModelScope.launch {
            val userFlow = userRepository.getUserBy(ownerId!!)
            val studyPlanFlow = studyPlanRepository.getStudyPlan(ownerId!!)
            studyPlanFlow.combine(userFlow) { studyPlanRes, userRes ->
                when {
                    studyPlanRes is Result.Success && userRes is Result.Success -> handleSuccess(
                        studyPlanRes.data
                    )
                    studyPlanRes is Result.Success && userRes is Result.Failed -> handleSuccess(
                        studyPlanRes.data
                    )
                    studyPlanRes is Result.Failed && userRes is Result.Success -> handleNoStudyPlanner(
                        userRes.data
                    )
                    studyPlanRes is Result.Failed && userRes is Result.Failed -> _state.value =
                        DataState(exception = studyPlanRes.message)
                    else -> _state.value = DataState(isLoading = true)
                }
            }.conflate().collect()
        }
    }

    private fun handleNoStudyPlanner(user: User) {
        val data = MyStudyPlanData(
            ownerId = ownerId!!,
            username = user.username,
            title = "Click me!",
            updatedLast = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            inEditMode = true,
        )
        _state.value = DataState(data)
    }

    private fun handleSuccess(studyPlan: StudyPlan) {
        val data = MyStudyPlanData(
            ownerId = ownerId!!,
            username = studyPlan.username,
            title = studyPlan.title,
            likes = studyPlan.likes,
            updatedLast = studyPlan.updated,
        )
        studyPlan.semesters.forEach { addSemester(it.order, it.courses) }
        studyPlan.semesters.forEach { expandSemester(it.order) }
        savedStudyPlan.value = studyPlan
        _state.value = DataState(data)
    }

    private fun isStudyPlanModified(unsavedStudyPlan: StudyPlan) =
        unsavedStudyPlan != savedStudyPlan.value

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

    fun changeEditMode(edit: Boolean) {
        _state.value = _state.value.copy(data = _state.value.data?.copy(inEditMode = edit))
    }

    fun editStudyPlanTitle(newTitle: String) {
        _state.value = _state.value.copy(data = _state.value.data?.copy(title = newTitle))
    }

    fun saveStudyPlan() {
        val unsavedStudyPlan = state.value.data
        val studyPlan = StudyPlan(
            userId = sharedPrefs.loggedInId!!,
            username = unsavedStudyPlan?.username ?: "",
            title = unsavedStudyPlan?.title ?: "",
            likes = unsavedStudyPlan?.likes ?: 0,
            semesters = semesterToCourses.map { Semester(it.key, it.value) }
        )

        if (isStudyPlanModified(studyPlan)) {
            viewModelScope.launch {
                studyPlanRepository.createStudyPlan(studyPlan).collect {
                    when (it) {
                        is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                        is Result.Failed -> _state.value =
                            _state.value.copy(data = _state.value.data?.copy(isUpdated = Event(false)))
                        is Result.Success -> _state.value =
                            _state.value.copy(data = _state.value.data?.copy(isUpdated = Event(true)))
                    }
                }
            }
        }
    }
}
