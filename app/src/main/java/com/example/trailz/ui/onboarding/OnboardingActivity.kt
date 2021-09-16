package com.example.trailz.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.trailz.MainActivity
import com.google.accompanist.pager.ExperimentalPagerApi

class OnboardingActivity: ComponentActivity() {
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Onboarding(finishOnboarding = ::navigateUp)
        }
    }

    private fun navigateUp(){
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit { putBoolean(COMPLETED_ONBOARDING_PREF, true) }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        const val COMPLETED_ONBOARDING_PREF = "completed_onboarding"
    }
}
