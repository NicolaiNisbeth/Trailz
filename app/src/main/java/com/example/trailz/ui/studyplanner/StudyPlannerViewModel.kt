package com.example.trailz.ui.studyplanner

import androidx.lifecycle.*
import com.example.base.Result
import com.example.base.domain.StudyPlan
import com.example.studyplan.StudyPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class StudyPlannerViewModel @Inject constructor(
    private val repository: StudyPlanRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val studyPlan: LiveData<StudyPlanUiModel> =
        savedStateHandle.getLiveData<String>("ownerId").switchMap { id ->
            liveData {
                repository.getStudyPlan(id).collect {
                    when (it){
                        is Result.Success -> emit(StudyPlanUiModel(studyPlan = it.data))
                        is Result.Loading -> emit(StudyPlanUiModel(loading = true))
                        is Result.Failed -> emit(StudyPlanUiModel(error = it.message))
                    }
                }
            }
        }
}

data class StudyPlanUiModel(
    val studyPlan: StudyPlan? = null,
    val error: String? = null,
    val loading: Boolean = false
)