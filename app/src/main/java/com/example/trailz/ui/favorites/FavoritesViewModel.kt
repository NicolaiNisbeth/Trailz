package com.example.trailz.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.Result
import com.example.base.domain.StudyPlan
import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.common.DataState
import com.example.trailz.ui.common.mapAsync
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
    private val favoriteRepository: FavoriteRepository,
    private val studyPlanRepository: StudyPlanRepository,
    private val prefs: SharedPrefs
) : ViewModel() {

    private val scope = viewModelScope
    private val _state: MutableStateFlow<DataState<StudyPlansUiModel>> = MutableStateFlow(
        DataState(isLoading = true)
    )
    val state: MutableStateFlow<DataState<StudyPlansUiModel>> = _state

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // get the users favorites
            val favoriteFlow = favoriteRepository.getFavoritesBy(prefs.loggedInId!!)
            val favoriteIds = favoriteFlow.mapNotNull {
                if (it is Result.Success) it.data.followedUserIds else null
            }

            // get study plan for each favorite
            val studyPlans = mutableListOf<StudyPlan>()
            favoriteIds.collect {
                // run N remote calls concurrently and await all
                 val studyPlanFlows = it.mapAsync {
                    studyPlanRepository.getStudyPlan(it).mapNotNull {
                        if (it is Result.Success)
                            it.data.copy(isFavorite = true)
                        else
                            null
                    }
                }
                studyPlanFlows.forEach { it.toCollection(studyPlans) }
            }
            _state.value = DataState(StudyPlansUiModel(studyPlans.filter { it.isFavorite }), isEmpty = studyPlans.isEmpty())
        }
    }

    fun updateFavorite(favoriteId: String, userId: String, isFavorite: Boolean){
        flipLocally(favoriteId)
        scope.launch {
            studyPlanRepository.updateStudyPlanFavorite(userId, isFavorite).collect()
            favoriteRepository.removeFromFavorite(favoriteId, userId).collect {
                when (it){
                    is Result.Failed -> _state.value = _state.value.copy(exception = it.message)
                    is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Result.Success -> {}
                }
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

    private fun flipLocally(favoriteId: String) {
        val data = _state.value.data ?: return
        val minusFavorited = data.copy(
            studyPlans = data.studyPlans.filterNot { it.userId == favoriteId },
            expandedPlans = data.expandedPlans.minus(favoriteId)
        )
        _state.value = _state.value.copy(
            data = minusFavorited,
            isEmpty = minusFavorited.studyPlans.isEmpty()
        )
    }
}