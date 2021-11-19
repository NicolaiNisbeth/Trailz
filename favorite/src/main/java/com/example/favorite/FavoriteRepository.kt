package com.example.favorite

import com.example.base.Result
import com.example.base.domain.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun observeFavoriteBy(userId: String): Flow<Result<Favorite>>

    suspend fun getFavoritesBy(userId: String): Result<Favorite>

    suspend fun addToFavorite(favoritedId: String, userId: String): Result<Unit>

    suspend fun removeFromFavorite(favoritedId: String, userId: String): Result<Unit>
}