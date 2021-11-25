package com.dtu.degree

import com.dtu.base.domain.Degree
import kotlinx.coroutines.flow.Flow
import com.dtu.base.Result

interface DegreeRepository {
    fun getDegrees(): Flow<Result<List<Degree>>>
    fun observeDegrees(): Flow<Result<List<Degree>>>
}