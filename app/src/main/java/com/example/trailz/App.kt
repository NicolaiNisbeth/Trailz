package com.example.trailz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.trailz.ui.onboarding.OnboardingActivity
import com.example.trailz.ui.onboarding.OnboardingActivity.Companion.COMPLETED_ONBOARDING_PREF

class App : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val completedOnboarding = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(COMPLETED_ONBOARDING_PREF, false)

        val destination = if (completedOnboarding) MainActivity::class.java
        else OnboardingActivity::class.java

        startActivity(Intent(this, destination))
        finish()
    }
}
