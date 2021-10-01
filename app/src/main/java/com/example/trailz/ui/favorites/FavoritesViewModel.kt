package com.example.trailz.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.Result
import com.example.base.domain.Favorite
import com.example.base.domain.StudyPlan
import com.example.favorite.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoriteRepository
) : ViewModel() {

    private val _favorite = MutableLiveData<Favorite>()
    val favorite: LiveData<Favorite>
        get() = _favorite

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String>
        get() = _isError

    fun initObserveFavoriteBy(userId: String){
        viewModelScope.launch {
            repository.observeFavoriteBy(userId).collect {
                when (it){
                    is Result.Failed -> {
                        _isError.value = it.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        val favorite = it.data
                        _favorite.value = favorite
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun addToFavorite(favoritedId: String, userId: String?){
        viewModelScope.launch {
            repository.addToFavorite(favoritedId, userId).collect {
                when (it){
                    is Result.Failed -> {
                        _isError.value = it.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun removeFromFavorite(favoritedId: String, userId: String?){
        viewModelScope.launch {
            repository.removeFromFavorite(favoritedId, userId).collect {
                when (it){
                    is Result.Failed -> {
                        _isError.value = it.message
                        _isLoading.value = false
                    }
                    is Result.Loading -> _isLoading.value = true
                    is Result.Success -> {
                        _isLoading.value = false
                    }
                }
            }
        }
    }
}