package com.dtu.trailz.ui.studyplans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dtu.base.Result
import com.dtu.favorite.FavoriteRepository
import com.dtu.studyplan.StudyPlanRepository
import com.dtu.trailz.inject.SharedPrefs
import com.dtu.trailz.ui.common.DataState
import com.dtu.trailz.ui.common.mapFind
import com.dtu.trailz.ui.favorites.StudyPlansUiModel
import com.dtu.trailz.ui.favorites.usecase.UpdateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyPlansViewModel @Inject constructor(
    private val studyPlanRepository: StudyPlanRepository,
    private val favoriteRepository: FavoriteRepository,
    private val sharedPrefs: SharedPrefs,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {

    private val scope = viewModelScope
    private val _state: MutableStateFlow<DataState<StudyPlansUiModel>> = MutableStateFlow(
        DataState(isLoading = true)
    )
    val state: MutableStateFlow<DataState<StudyPlansUiModel>> = _state

    init {
        observeStudyPlans()
    }

    private fun observeStudyPlans() {
        scope.launch {
            val favorites = when (val res = favoriteRepository.getFavoritesBy(sharedPrefs.loggedInId!!)){
                is Result.Success -> res.data.followedUserIds
                is Result.Failed -> emptyList()
                is Result.Loading -> emptyList()
            }.toHashSet()

            studyPlanRepository.getStudyPlans().collect {
                when (it) {
                    is Result.Failed -> _state.value = _state.value.copy(exception = it.message)
                    is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Result.Success -> {
                        val studyPlans = it.data.map { it.copy(isFavorite = it.userId in favorites) }
                        _state.value = DataState(
                            StudyPlansUiModel(studyPlans),
                            isEmpty = studyPlans.isEmpty()
                        )
                    }
                }
            }
        }
    }

    fun refreshStudyPlans(isForced: Boolean){
        scope.launch {
            studyPlanRepository.refreshStudyPlansIfStale(isForced).collect {
                val favorites = when (val res = favoriteRepository.getFavoritesBy(sharedPrefs.loggedInId!!)){
                    is Result.Success -> res.data.followedUserIds
                    is Result.Failed -> emptyList()
                    is Result.Loading -> emptyList()
                }.toHashSet()
                when (it) {
                    is Result.Failed -> _state.value = _state.value.copy(exception = it.message)
                    is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Result.Success -> {
                        val studyPlans = it.data.map { it.copy(isFavorite = it.userId in favorites) }
                        _state.value = DataState(
                            StudyPlansUiModel(studyPlans),
                            isEmpty = studyPlans.isEmpty()
                        )
                    }
                }
            }
        }
    }

    fun updateFavorite(studyPlanId: String, isFavorite: Boolean, likes: Long) {
        scope.launch {
            flipLocally(studyPlanId, isFavorite, likes)
            updateFavoriteUseCase(studyPlanId, sharedPrefs.loggedInId!!, isFavorite, likes)
        }
    }

    fun updateExpanded(studyPlanId: String){
        val data = _state.value.data ?: return
        val state = data.copy(
            expandedPlans = data.expandedPlans.toMutableMap().apply {
                this[studyPlanId] = this[studyPlanId]?.not() ?: false
            }
        )
        _state.value = _state.value.copy(
            data = state,
            isEmpty = state.studyPlans.isEmpty()
        )
    }

    private fun flipLocally(favoriteId: String, isFavorite: Boolean, likes: Long) {
        val data = _state.value.data ?: return
        val newData = data.copy(
            studyPlans = data.studyPlans.mapFind(
                predicate = { it.userId == favoriteId },
                transform = {
                    it.copy(
                        isFavorite = isFavorite,
                        likes = if (isFavorite) likes.plus(1) else likes.minus(1)
                    )
                }
            )
        )
        _state.value = _state.value.copy(
            data = newData,
            isEmpty = newData.studyPlans.isEmpty()
        )
    }
}