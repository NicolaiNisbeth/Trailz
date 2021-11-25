package com.dtu.trailz.ui.studyplan

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.*
import com.dtu.base.Result
import com.dtu.base.domain.Course
import com.dtu.base.domain.Semester
import com.dtu.base.domain.StudyPlan
import com.dtu.base.domain.User
import com.dtu.studyplan.StudyPlanRepository
import com.dtu.trailz.inject.SharedPrefs
import com.dtu.trailz.ui.common.DataState
import com.dtu.trailz.ui.common.Event
import com.dtu.user.UserRepository
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
class StudyPlanViewModel @Inject constructor(
    private val studyPlanRepository: StudyPlanRepository,
    private val userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val ownerId = savedStateHandle.get<String>("ownerId") ?: sharedPrefs.loggedInId
    private val savedStudyPlan = MutableLiveData<StudyPlan>()
    private val studyPlanUtil = StudyPlanUtil(mutableStateMapOf())
    var semesterToCourses = studyPlanUtil.semesterToCourses

    private val _state: MutableStateFlow<DataState<MyStudyPlanData>> = MutableStateFlow(
        DataState(isLoading = true)
    )
    val state: StateFlow<DataState<MyStudyPlanData>> = _state

    init {
        viewModelScope.launch {
            val userFlow = userRepository.getUserBy(ownerId!!)
            val studyPlanFlow = studyPlanRepository.getStudyPlan(ownerId)
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
        studyPlan.semesters.forEach { studyPlanUtil.addSemester(it.order, it.courses) }
        studyPlan.semesters.forEach { studyPlanUtil.expandSemester(it.order) }
        savedStudyPlan.value = studyPlan
        _state.value = DataState(data)
    }

    private fun isStudyPlanModified(unsavedStudyPlan: StudyPlan) =
        unsavedStudyPlan != savedStudyPlan.value

    fun changeEditMode(edit: Boolean) {
        _state.value = _state.value.copy(data = _state.value.data?.copy(inEditMode = edit))
    }

    fun editStudyPlanTitle(newTitle: String) {
        _state.value = _state.value.copy(data = _state.value.data?.copy(title = newTitle.trim()))
    }

    fun saveStudyPlan() {
        val unsavedStudyPlan = state.value.data
        val studyPlan = StudyPlan(
            userId = sharedPrefs.loggedInId!!,
            username = unsavedStudyPlan?.username ?: "",
            title = unsavedStudyPlan?.title ?: "",
            likes = unsavedStudyPlan?.likes ?: 0,
            updated = unsavedStudyPlan?.updatedLast ?: "",
            semesters = semesterToCourses.map { Semester(it.key, it.value) }
        )

        if (isStudyPlanModified(studyPlan)) {
            viewModelScope.launch {
                studyPlanRepository.createMyStudyPlan(studyPlan).collect {
                    when (it) {
                        is Result.Loading -> _state.value =
                            _state.value.copy(isLoading = true)
                        is Result.Failed -> _state.value =
                            _state.value.copy(data = _state.value.data?.copy(isUpdated = Event(false)))
                        is Result.Success -> _state.value =
                            _state.value.copy(data = _state.value.data?.copy(isUpdated = Event(true)))
                    }
                }
            }
        }
    }
    fun isSemesterCollapsed(semesterId: Int) = studyPlanUtil.isSemesterCollapsed(semesterId)

    fun isAnyCollapsed() = studyPlanUtil.isAnyCollapsed()

    fun collapseSemester(semesterId: Int) {
        studyPlanUtil.collapseSemester(semesterId)
    }

    fun expandSemester(semesterID: Int) {
       studyPlanUtil.expandSemester(semesterID)
    }

    fun toggleAllSemesters(allCollapsed: Boolean) {
        studyPlanUtil.flipAllSemesterVisibility(allCollapsed)
    }

    fun addSemester(semesterId: Int? = null, courses: List<Course> = emptyList()) {
        studyPlanUtil.addSemester(semesterId, courses)
    }

    fun removeSemester(semesterId: Int) {
        studyPlanUtil.removeSemester(semesterId)
    }

    fun editSemester(id: Int, newTitle: String) {
        studyPlanUtil.editSemester(id, newTitle.trim())
    }

    fun addCourse(course: Course, semesterId: Int) {
        studyPlanUtil.addCourse(course, semesterId)
    }

    fun removeCourse(course: Course, semesterId: Int) {
       studyPlanUtil.removeCourse(course, semesterId)
    }

    fun replaceCourseAt(index: Int, semesterId: Int, course: Course) {
        studyPlanUtil.replaceCourseAt(index, semesterId, course)
    }
}
