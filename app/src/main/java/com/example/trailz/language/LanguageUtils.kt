package com.example.trailz.language

import androidx.annotation.DrawableRes
import com.example.trailz.R
import java.util.*

enum class LanguageConfig(
    val title: String,
    val code: String,
    val language: String,
    @DrawableRes val flagResource: Int,
) {

    DK("Danmark", "dk", "da", R.drawable.flag_dk,),
    EU("Europe", "eu", "en", R.drawable.flag_eu);

    companion object {
        fun codeToLanguage(code: String): String {
            return when(code){
                DK.code -> DK.language
                EU.code -> EU.language
                else -> EU.language
            }
        }
    }
}