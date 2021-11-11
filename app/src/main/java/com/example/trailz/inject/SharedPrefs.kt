package com.example.trailz.inject

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class SharedPrefs(
    private val context: Context
) {
    var loggedInId: String?
        get() = getDefaultSharedPreferences(context).getString("in_memory_user_id", null)
        set(value) = getDefaultSharedPreferences(context).edit { putString("in_memory_user_id", value) }

    var languagePreference: String?
        get() = getDefaultSharedPreferences(context).getString("in_memory_language_preference", "en")
        set(value) = getDefaultSharedPreferences(context).edit { putString("in_memory_language_preference", value) }

    var isDarkTheme: Boolean
        get() = getDefaultSharedPreferences(context).getBoolean("in_memory_theme_preference", false)
        set(value) = getDefaultSharedPreferences(context).edit { putBoolean("in_memory_theme_preference", value) }
}
