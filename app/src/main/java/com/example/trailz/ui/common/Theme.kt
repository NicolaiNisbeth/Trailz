package com.example.trailz.ui.common

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

fun Context.themeColor(@AttrRes attrRes: Int) = TypedValue()
    .run {
        theme.resolveAttribute (attrRes, this, true)
        data
    }
