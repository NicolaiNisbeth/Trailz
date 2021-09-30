package com.example.favorite

import com.example.base.Result
import com.example.base.domain.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun observeFavoriteBy(userId: String): Flow<Result<Favorite>>

    fun addToFavorite(favoritedId: String, userId: String): Flow<Result<Unit>>

    fun removeFromFavorite(favoritedId: String, userId: String): Flow<Result<Unit>>
}