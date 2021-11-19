package com.example.trailz.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.example.trailz.BaseActivity
import com.example.trailz.MainActivity
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.login.LoginActivity
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity: BaseActivity() {

    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MdcTheme {
                Onboarding(finishOnboarding = ::navigateUp)
            }
        }
    }

    private fun navigateUp(){
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit { putBoolean(COMPLETED_ONBOARDING_PREF, true) }

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        const val COMPLETED_ONBOARDING_PREF = "completed_onboarding"
    }
}
