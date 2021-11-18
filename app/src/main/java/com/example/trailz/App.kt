package com.example.trailz

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.login.LoginActivity
import com.example.trailz.ui.onboarding.OnboardingActivity
import com.example.trailz.ui.onboarding.OnboardingActivity.Companion.COMPLETED_ONBOARDING_PREF
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class App : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val completedOnboarding = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(COMPLETED_ONBOARDING_PREF, false)

        val destination = if (completedOnboarding) {
            if (prefs.loggedInId != null){
                MainActivity::class.java
            } else {
                LoginActivity::class.java
            }
        } else {
            OnboardingActivity::class.java
        }

        startActivity(Intent(this, destination))
        finish()
    }
}
