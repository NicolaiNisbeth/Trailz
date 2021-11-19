package com.example.trailz.ui.studyplans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.Result
import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.mapFind
import com.example.trailz.ui.favorites.StudyPlansUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyPlansViewModel @Inject constructor(
    private val studyPlanRepository: StudyPlanRepository,
    private val favoriteRepository: FavoriteRepository,
    private val sharedPrefs: SharedPrefs,
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
            val studyPlansFlow = studyPlanRepository.getStudyPlans()
            val favoritesFlow = favoriteRepository.observeFavoriteBy(sharedPrefs.loggedInId!!)
            studyPlansFlow.combine(favoritesFlow) { studyPlansRes, favoritesRes ->
                when {
                    studyPlansRes is Result.Success && favoritesRes is Result.Success -> {
                        val favorites = favoritesRes.data.followedUserIds.toHashSet()
                        val studyPlans = studyPlansRes.data.map { it.copy(isFavorite = it.userId in favorites) }
                        _state.value = DataState(
                            StudyPlansUiModel(studyPlans),
                            isEmpty = studyPlans.isEmpty()
                        )
                    }
                    studyPlansRes is Result.Success && favoritesRes is Result.Failed -> {
                        _state.value = DataState(
                            StudyPlansUiModel(studyPlansRes.data),
                            isEmpty = studyPlansRes.data.isEmpty()
                        )
                    }
                    studyPlansRes is Result.Failed && favoritesRes is Result.Success -> {
                        _state.value = _state.value.copy(
                            exception = "failed loading study plans"
                        )
                    }
                    else -> _state.value = _state.value.copy(isLoading = true)
                }
            }.conflate().collect()
        }
    }

    fun updateChecked(studyPlanId: String, isFavorite: Boolean) {
        flipLocally(studyPlanId, isFavorite)
        scope.launch {
            studyPlanRepository.updateStudyPlanFavorite(studyPlanId, isFavorite).collect()
            if (isFavorite) {
                favoriteRepository.addToFavorite(studyPlanId, sharedPrefs.loggedInId!!)
            } else {
                favoriteRepository.removeFromFavorite(studyPlanId, sharedPrefs.loggedInId!!)
            }
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

    private fun flipLocally(favoriteId: String, isFavorite: Boolean) {
        val data = _state.value.data ?: return
        val newData = data.copy(
            studyPlans = data.studyPlans.mapFind(
                predicate = { it.userId == favoriteId },
                transform = { it.copy(isFavorite = isFavorite) }
            )
        )
        _state.value = _state.value.copy(
            data = newData,
            isEmpty = newData.studyPlans.isEmpty()
        )
    }
}