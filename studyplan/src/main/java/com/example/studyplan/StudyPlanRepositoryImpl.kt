package com.example.studyplan

import android.os.SystemClock
import com.example.base.domain.StudyPlan
import com.example.base.Result
import com.example.studyplan.local.StudyPlanLocalDataSource
import com.example.studyplan.remote.StudyPlanRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.*

class StudyPlanRepositoryImpl(
    private val localDataSource: StudyPlanLocalDataSource,
    private val remoteDataSource: StudyPlanRemoteDataSource,
    private val cacheUtil: CacheUtil
) : StudyPlanRepository {

    companion object {
        const val STUDY_PLAN_CACHE_KEY = "study_play_cache_key"
    }

    override suspend fun observeStudyPlan(id: String) = flow<Result<StudyPlan>> {
        val plan = localDataSource.getStudyPlan(id)
        if (plan != null) emit(Result.success(plan))
        else emit(Result.failed("No studyPlan with id:$id"))
        remoteDataSource.observeStudyPlan(id).collect {
            if (it is Result.Success){
                emit(Result.success(it.data))
                remoteDataSource.createStudyPlan(it.data).collect()
                cacheUtil.updateTimer(STUDY_PLAN_CACHE_KEY, System.currentTimeMillis())
            }
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun getStudyPlan(id: String) = flow<Result<StudyPlan>> {
        val studyPlan = localDataSource.getStudyPlan(id)
        if (studyPlan != null) emit(Result.success(studyPlan))
        else emit(Result.failed("No studyPlan with id:$id"))
        val res = remoteDataSource.getStudyPlan(id)
        if (res is Result.Success){
            emit(Result.success(res.data))
            remoteDataSource.createStudyPlan(res.data).collect()
            cacheUtil.updateTimer(STUDY_PLAN_CACHE_KEY, System.currentTimeMillis())
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun getStudyPlans() = flow<Result<List<StudyPlan>>> {
        emit(Result.success(localDataSource.getStudyPlans()))
        refreshStudyPlansIfStale().collect { emit(it) }
    }.flowOn(Dispatchers.Default)

    override suspend fun createMyStudyPlan(studyPlan: StudyPlan) = flow<Result<Unit>> {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentDT: String = simpleDateFormat.format(Date())
        val studyPlanDate = studyPlan.copy(updated = currentDT)
        remoteDataSource.createStudyPlan(studyPlanDate).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean, likes: Long) = flow<Unit> {
        localDataSource.updateStudyPlanFavorite(id, isFavorite, likes)
        val result = remoteDataSource.updateStudyPlanFavorite(id, isFavorite)
        if (result is Result.Failed){
            localDataSource.updateStudyPlanFavorite(id, !isFavorite, likes)
        }
    }

    override suspend fun refreshStudyPlansIfStale(isForced: Boolean) = flow<Result<List<StudyPlan>>> {
        val isStale = cacheUtil.isDataStale(STUDY_PLAN_CACHE_KEY, System.currentTimeMillis())
        if (isForced || isStale) {
            emit(Result.loading())
            delay(1500) // FIXME: For demonstration purposes
            when (val result = remoteDataSource.getStudyPlans()) {
                is Result.Failed -> emit(Result.failed(result.message))
                is Result.Success -> {
                    localDataSource.createStudyPlans(result.data)
                    emit(Result.success(result.data))
                }
                is Result.Loading -> emit(Result.loading())
            }
        }
    }.flowOn(Dispatchers.Default)
}
