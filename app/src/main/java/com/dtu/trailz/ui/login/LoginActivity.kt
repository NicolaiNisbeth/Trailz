package com.dtu.trailz.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import com.dtu.trailz.BaseActivity
import com.dtu.trailz.MainActivity
import com.dtu.trailz.R
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
