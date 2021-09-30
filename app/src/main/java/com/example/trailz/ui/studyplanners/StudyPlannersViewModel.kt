package com.example.trailz.ui.studyplanners

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyplan.Result
import com.example.studyplan.StudyPlanRepository
import com.example.studyplan.domain.Course
import com.example.studyplan.domain.Semester
import com.example.studyplan.domain.StudyPlan
import com.example.trailz.ui.marketplace.StudyPLansRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyPlannersViewModel @Inject constructor(
    private val repository: StudyPlanRepository
) : ViewModel() {

    private val _studyplans = MutableLiveData<List<StudyPlan>>()
    val studyPlans: LiveData<List<StudyPlan>>
        get() = _studyplans

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String>
        get() = _isError

    init {
        viewModelScope.launch {
            repository.observeStudyPlans().collect {
                when (it){
                    is Result.Failed -> {
                        _isError.value = it.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        val studyPlans = it.data
                        _studyplans.value = studyPlans
                        _isLoading.value = false
                    }
                }
            }
        }

    }
}