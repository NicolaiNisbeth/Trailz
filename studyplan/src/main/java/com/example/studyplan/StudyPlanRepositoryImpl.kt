package com.example.studyplan

import com.example.base.domain.StudyPlan
import com.example.base.Result
import com.example.studyplan.local.StudyPlanLocalDataSource
import com.example.studyplan.remote.StudyPlanRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StudyPlanRepositoryImpl(
    private val localDataSource: StudyPlanLocalDataSource,
    private val remoteDataSource: StudyPlanRemoteDataSource,
) : StudyPlanRepository {

    override suspend fun observeStudyPlan(id: String) = flow<Result<StudyPlan>> {
        localDataSource.observeStudyPlan(id).collect {
            if (it != null) emit(Result.success(it))
            else emit(Result.failed("No studyPlan with id:$id"))
        }
        remoteDataSource.observeStudyPlan(id).collect {
            when (it) {
                is Result.Success -> createStudyPlan(it.data)
                else -> { }
            }
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun getStudyPlan(id: String) = flow<Result<StudyPlan>> {
        emit(Result.loading())
        val studyPlan = localDataSource.getStudyPlan(id)
        if (studyPlan != null) emit(Result.success(studyPlan))
        else emit(Result.failed("No studyPlan with id:$id"))
        when (val res = remoteDataSource.getStudyPlan(id)) {
            is Result.Success -> localDataSource.createStudyPlan(res.data)
            else -> { }
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun getStudyPlans() = flow<Result<List<StudyPlan>>> {
        localDataSource.observeStudyPlans().collect { emit(Result.Success(it)) }
        refreshStudyPlansIfStale(true).collect { emit(it) }
    }.flowOn(Dispatchers.Default)

    override suspend fun createStudyPlan(studyPlan: StudyPlan) = flow<Result<Unit>> {
        emit(Result.loading())
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentDT: String = simpleDateFormat.format(Date())
        val studyPlanDate = studyPlan.copy(updated = currentDT)
        remoteDataSource.createStudyPlan(studyPlanDate).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean) = flow<Unit> {
        localDataSource.updateStudyPlanFavorite(id, isFavorite)
    }.flowOn(Dispatchers.Default)

    fun refreshStudyPlansIfStale(forced: Boolean = false) = flow<Result<List<StudyPlan>>> {
        emit(Result.loading())
        if (forced) {
            when (val result = remoteDataSource.getStudyPlans()) {
                is Result.Failed -> emit(Result.failed(result.message))
                is Result.Loading -> { }
                is Result.Success -> localDataSource.createStudyPlans(result.data)
            }
        }
    }.flowOn(Dispatchers.Default)
}
