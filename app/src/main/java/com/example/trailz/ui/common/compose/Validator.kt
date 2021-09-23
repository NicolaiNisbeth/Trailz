package com.example.trailz.ui.common.compose

fun invalidInput(email: String?, password: String?) = email.isNullOrBlank() || password.isNullOrBlank()
fun invalidInput(
    username: String?,
    email: String?,
    password: String?,
    studyPath: String?
) = username.isNullOrBlank() || email.isNullOrBlank() || password.isNullOrBlank() || studyPath.isNullOrBlank()
