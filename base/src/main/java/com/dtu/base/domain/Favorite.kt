package com.dtu.base.domain

data class Favorite(
    val userId: String = "",
    val followedUserIds: List<String> = emptyList()
)