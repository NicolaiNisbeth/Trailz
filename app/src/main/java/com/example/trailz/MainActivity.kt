package com.example.trailz

import android.content.Context
import android.content.Intent
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
import com.example.trailz.ui.login.LoginActivity
import kotlinx.coroutines.flow.MutableStateFlow


@AndroidEntryPoint
class MainActivity : BaseActivity(), LogoutListener {

    @Inject
    lateinit var application: TrailzApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomNavigationView = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .navView

        bottomNavigationView.setupWithNavController(
            findNavController(R.id.nav_host_fragment_activity_main)
        )
        bottomNavigationView.setOnItemReselectedListener {
            /* to avoid recreating fragment when fragment is reselected */
        }
    }

    override fun onLogout() {
        sharedPrefs.loggedInId = null
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

interface LogoutListener {
    fun onLogout()
}

