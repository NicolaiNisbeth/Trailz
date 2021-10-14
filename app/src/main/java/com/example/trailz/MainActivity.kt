package com.example.trailz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trailz.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.*
import com.example.base.Result
import com.example.favorite.FavoriteRepository
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.common.themeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomNavigationView = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .navView

        bottomNavigationView.setupWithNavController(
            findNavController(R.id.nav_host_fragment_activity_main)
        )

        val colorSecondary = themeColor(R.attr.colorSecondary)
        val colorOnSecondary = themeColor(R.attr.colorOnSecondary)
        val favoriteBadge = bottomNavigationView.getOrCreateBadge(R.id.favorites_navigation).apply {
            backgroundColor = colorSecondary
            badgeTextColor = colorOnSecondary
        }
        viewModel.count.observe(this) {
            favoriteBadge.isVisible = it > 0
            favoriteBadge.number = it
        }
    }
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: FavoriteRepository,
    private val sharedPref: SharedPrefs
): ViewModel(){

    private val _count = MutableLiveData<Int>()
    val count: LiveData<Int> = _count

    init {
        viewModelScope.launch {
            repository.observeFavoriteBy(sharedPref.loggedInId).collect {
                when (it){
                    is Result.Failed -> { }
                    is Result.Loading -> { }
                    is Result.Success -> {
                        _count.value = it.data.followedUserIds.count()
                    }
                }
            }
        }
    }
}
