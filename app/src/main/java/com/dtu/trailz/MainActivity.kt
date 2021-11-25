package com.dtu.trailz

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.dtu.trailz.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.*
import androidx.navigation.ui.setupWithNavController
import javax.inject.Inject

import com.dtu.trailz.ui.login.LoginActivity


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

