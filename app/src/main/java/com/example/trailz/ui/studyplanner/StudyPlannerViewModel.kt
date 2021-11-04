package com.example.trailz.ui.studyplanner

import androidx.lifecycle.*
import com.example.base.Result
import com.example.base.domain.StudyPlan
import com.example.studyplan.StudyPlanRepository
import com.example.trailz.ui.studyplanners.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyPlannerViewModel @Inject constructor(
    private val repository: StudyPlanRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val scope = viewModelScope
    val _state: MutableStateFlow<DataState<StudyPlan>> = MutableStateFlow(
        DataState(isLoading = true)
    )

    val state: StateFlow<DataState<StudyPlan>> = _state

    init {
        observeStudyPlan()
    }

    private fun observeStudyPlan() {
        scope.launch {
            savedStateHandle.get<String>("ownerId")?.let { id ->
                repository.getStudyPlan(id).collect {
                    when (it){
                        is Result.Failed -> _state.value = _state.value.copy(exception = it.message)
                        is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                        is Result.Success -> _state.value = DataState(it.data)
                    }
                }
            }
        }
    }
}

