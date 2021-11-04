package com.example.trailz.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.Result
import com.example.base.domain.StudyPlan
import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.common.mapAsync
import com.example.trailz.ui.studyplanners.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val studyPlanRepository: StudyPlanRepository,
    private val prefs: SharedPrefs
) : ViewModel() {

    private val scope = viewModelScope
    private val _state: MutableStateFlow<DataState<List<StudyPlan>>> = MutableStateFlow(
        DataState(isLoading = true)
    )
    val state: MutableStateFlow<DataState<List<StudyPlan>>> = _state

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        scope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // get the users favorites
            val favoriteFlow = favoriteRepository.getFavoritesBy(prefs.loggedInId)
            val favoriteIds = favoriteFlow.mapNotNull {
                if (it is Result.Success) it.data.followedUserIds else null
            }

            // get study plan for each favorite
            val studyPlans = mutableListOf<StudyPlan>()
            favoriteIds.collect {
                // run N remote calls concurrently and await all
                 val studyPlanFlows = it.mapAsync {
                    studyPlanRepository.getStudyPlan(it).mapNotNull {
                        if (it is Result.Success) it.data else null
                    }
                }
                studyPlanFlows.forEach { it.toCollection(studyPlans) }
            }
            _state.value = DataState(studyPlans, isEmpty = studyPlans.isEmpty())
        }
    }

    fun updateFavorite(favoriteId: String, userId: String, isFavorite: Boolean){
        flipLocally(favoriteId)
        val result = if (isFavorite) favoriteRepository.removeFromFavorite(favoriteId, userId)
        else favoriteRepository.addToFavorite(favoriteId, userId)

        scope.launch {
            result.collect {
                when (it){
                    is Result.Failed -> _state.value = _state.value.copy(exception = it.message)
                    is Result.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Result.Success -> {}
                }
            }
        }
    }

    private fun flipLocally(favoriteId: String) {
        val studyPlans = _state.value.data ?: return
        val minusFavorited = studyPlans.filterNot { it.userId == favoriteId }
        _state.value = _state.value.copy(
            data = minusFavorited,
            isEmpty = minusFavorited.isEmpty()
        )
    }
}