package com.example.trailz.ui.common.compose

import android.util.Patterns

fun invalidInput(email: String?, password: String?) = email.isNullOrBlank() || password.isNullOrBlank()

fun invalidInput(
    username: String?,
    email: String?,
    password: String?,
    studyPath: String?
) = username.isNullOrBlank() || isEmailValid(email) || password.isNullOrBlank() || studyPath.isNullOrBlank()

fun isEmailValid(email: String?): Boolean {
    return email.isNullOrBlank().not() && Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
}