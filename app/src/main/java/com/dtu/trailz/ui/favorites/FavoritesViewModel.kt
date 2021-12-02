package com.dtu.trailz.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dtu.base.Result
import com.dtu.base.domain.StudyPlan
import com.dtu.trailz.inject.SharedPrefs
import com.dtu.trailz.ui.common.DataState
import com.dtu.trailz.ui.favorites.usecase.GetFavoritesUseCase
import com.dtu.trailz.ui.favorites.usecase.UpdateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudyPlansUiModel(
    val studyPlans: List<StudyPlan>,
    val expandedPlans: Map<String, Boolean> = studyPlans.associate { it.userId to false }
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val prefs: SharedPrefs
) : ViewModel() {

    private val scope = viewModelScope
    private val _state = MutableStateFlow<DataState<StudyPlansUiModel>>(
        DataState(isLoading = true)
    )
    val state: StateFlow<DataState<StudyPlansUiModel>> = _state

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        scope.launch {
            getFavoritesUseCase(prefs.loggedInId!!).collect {
                when(it){
                    is Result.Failed -> _state.value = _state.value.copy(exception = it.message)
                    is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Result.Success -> _state.value = DataState(
                        data = StudyPlansUiModel(it.data),
                        isEmpty = it.data.isEmpty()
                    )
                }
            }
        }
    }

    fun updateFavorite(favoriteId: String, isFavorite: Boolean, likes: Long){
        scope.launch {
            flipLocally(favoriteId)
            updateFavoriteUseCase(favoriteId, prefs.loggedInId!!, isFavorite, likes)
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

    private fun flipLocally(favoriteId: String) {
        val data = _state.value.data ?: return
        val minusFavorite = data.copy(
            studyPlans = data.studyPlans.filterNot { it.userId == favoriteId },
            expandedPlans = data.expandedPlans.minus(favoriteId)
        )
        _state.value = _state.value.copy(
            data = minusFavorite,
            isEmpty = minusFavorite.studyPlans.isEmpty()
        )
    }
}