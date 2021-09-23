package com.example.trailz

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.language.LanguageConfig
import java.util.*
import javax.inject.Inject

open class BaseActivity: AppCompatActivity(), ChangeLanguageListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun attachBaseContext(context: Context) {
        val country = PreferenceManager.getDefaultSharedPreferences(context)
            .getString("in_memory_language_preference", "en") ?: "en"
        super.attachBaseContext(setLocale(context, Locale(country)))
    }

    private fun setLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        return context.createConfigurationContext(
            Configuration(context.resources.configuration).apply {
                setLocale(locale)
            }
        )
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        // This is a workaround for a Framework issue in versions prior to SDK 26 where the desired
        // configuration gets overridden.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) resources
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onChangeLanguage(code: String) {
        sharedPrefs.languagePreference = LanguageConfig.codeToLanguage(code)
        finish()
        restartApp()

    }

    private fun restartApp() {
        val app = application as TrailzApplication
        val newApp = Intent(application as TrailzApplication, App::class.java)
        newApp.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        app.startActivity(newApp)
    }
}

fun interface ChangeLanguageListener {
    fun onChangeLanguage(code: String)
}