package com.example.trailz.ui.common.compose

fun invalidInput(email: String, password: String) = email.isBlank() || password.isBlank()
