package com.dtu.trailz.language

import androidx.annotation.DrawableRes
import com.dtu.trailz.R

enum class LanguageConfig(
    val title: String,
    val code: String,
    val language: String,
    @DrawableRes val flagResource: Int,
) {

    DK("Dansk", "dk", "da", R.drawable.flag_denmark,),
    EU("English", "eu", "en", R.drawable.flag_united_kingdom);

    companion object {
        fun codeToConfig(code: String?): LanguageConfig {
            return when(code){
                DK.code -> DK
                EU.code -> EU
                else -> EU
            }
        }
        fun languageToConfig(language: String?): LanguageConfig {
            return when (language){
                DK.language -> DK
                EU.language -> EU
                else -> EU
            }
        }
    }
}