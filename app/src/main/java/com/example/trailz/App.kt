package com.example.trailz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.trailz.ui.login.LoginActivity
import com.example.trailz.ui.onboarding.OnboardingActivity
import com.example.trailz.ui.onboarding.OnboardingActivity.Companion.COMPLETED_ONBOARDING_PREF
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class App : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        val completedOnboarding = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(COMPLETED_ONBOARDING_PREF, false)

        val destination = if (completedOnboarding) LoginActivity::class.java
        else OnboardingActivity::class.java

        startActivity(Intent(this, destination))
        finish()
    }
}
