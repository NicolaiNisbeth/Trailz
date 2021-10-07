package com.example.degree

import com.example.base.domain.Degree
import kotlinx.coroutines.flow.Flow
import com.example.base.Result

interface DegreeRepository {
    fun getDegrees(): Flow<Result<List<Degree>>>
    fun observeDegrees(): Flow<Result<List<Degree>>>
}