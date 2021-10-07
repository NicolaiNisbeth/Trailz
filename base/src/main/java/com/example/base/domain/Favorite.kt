package com.example.base.domain

import com.example.base.domain.StudyPlan

data class Favorite(
    val userId: String = "",
    val followedUserIds: List<String> = emptyList()
)