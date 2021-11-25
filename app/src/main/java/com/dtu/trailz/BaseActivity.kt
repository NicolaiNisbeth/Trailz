package com.dtu.trailz

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.dtu.trailz.inject.SharedPrefs
import com.dtu.trailz.language.LanguageConfig
import java.util.*
import javax.inject.Inject

open class BaseActivity: AppCompatActivity(), ChangeLanguageListener, OpenSettingsListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var prefs: SharedPrefs

    override fun attachBaseContext(context: Context) {
        val country = PreferenceManager.getDefaultSharedPreferences(context)
            .getString("in_memory_language_preference", "en") ?: "en"
        super.attachBaseContext(setLocale(context, Locale(country)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (prefs.isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
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
        val country = LanguageConfig.codeToConfig(code)
        sharedPrefs.languagePreference = country.language
        finish()
        restartApp()
    }

    private fun restartApp() {
        val app = application as TrailzApplication
        val newApp = Intent(application as TrailzApplication, App::class.java)
        newApp.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        app.startActivity(newApp)
    }

    override fun onOpenSettingsListener() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }
}

fun interface ChangeLanguageListener {
    fun onChangeLanguage(code: String)
}

fun interface OpenSettingsListener {
    fun onOpenSettingsListener()
}
