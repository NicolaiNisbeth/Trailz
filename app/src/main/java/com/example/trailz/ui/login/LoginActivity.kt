package com.example.trailz.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.example.trailz.BaseActivity
import com.example.trailz.MainActivity
import com.example.trailz.R
import com.example.trailz.databinding.ActivityLoginBinding
import com.example.trailz.databinding.ActivityMainBinding
import com.example.trailz.ui.onboarding.OnboardingActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity: BaseActivity(), LoginListener {

    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val navHost = findNavController(R.id.nav_host_fragment_activity_login)
        navHost.setGraph(R.navigation.login_navigation)
    }

    override fun onLoginSuccess() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

interface LoginListener {
    fun onLoginSuccess()
}
