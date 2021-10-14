package com.example.trailz

import android.content.Context
import android.graphics.Color
import android.graphics.Point
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
import android.view.WindowManager

import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatDelegate


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var application: TrailzApplication

    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        /* FIXME: vi må lige rydde op i det her snask
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
         */
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
