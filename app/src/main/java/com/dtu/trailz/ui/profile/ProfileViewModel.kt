package com.dtu.trailz.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dtu.base.Result
import com.dtu.base.domain.User
import com.dtu.studyplan.StudyPlanRepository
import com.dtu.trailz.inject.SharedPrefs
import com.dtu.trailz.ui.common.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dtu.user.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate

data class ProfileData(
    val user: User,
    val likes: Long
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val studyPlan: StudyPlanRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _state = MutableLiveData<DataState<ProfileData>>(DataState(isLoading = true))
    val state: LiveData<DataState<ProfileData>> = _state

    init {
        viewModelScope.launch {
            val userFlow = userRepository.getUserBy(sharedPrefs.loggedInId!!)
            val studyPlanFlow = studyPlan.observeStudyPlan(sharedPrefs.loggedInId!!)
            userFlow.combine(studyPlanFlow){ userRes, studyPlanRes ->
                 when {
                    userRes is Result.Success && studyPlanRes is Result.Success -> {
                        _state.value = DataState(data = ProfileData(userRes.data, studyPlanRes.data.likes))
                    }
                    userRes is Result.Success && studyPlanRes is Result.Failed -> {
                        _state.value = DataState(data = ProfileData(userRes.data, 0))
                    }
                    userRes is Result.Failed && studyPlanRes is Result.Success -> {
                        _state.value = _state.value?.copy(exception = userRes.message)
                    }
                    userRes is Result.Failed && studyPlanRes is Result.Failed -> {
                        _state.value = _state.value?.copy(exception = userRes.message)
                    }
                    else -> _state.value = _state.value?.copy(isLoading = true)
                }
            }.conflate().collect()
        }
    }
}
