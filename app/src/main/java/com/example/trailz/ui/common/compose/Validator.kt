package com.example.trailz.ui.common.compose

fun invalidInput(email: String, password: String) = email.isBlank() || password.isBlank()
fun invalidInput(
    username: String,
    email: String,
    password: String,
    studyPath: String
) = username.isBlank() || email.isBlank() || password.isBlank() || studyPath.isBlank()
