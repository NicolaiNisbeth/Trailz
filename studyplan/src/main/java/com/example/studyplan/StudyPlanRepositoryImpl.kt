package com.example.studyplan

import com.example.base.domain.StudyPlan
import com.example.base.Result
import com.example.studyplan.local.StudyPlanLocalDataSource
import com.example.studyplan.remote.StudyPlanRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
class StudyPlanRepositoryImpl(
    private val localDataSource: StudyPlanLocalDataSource,
    private val remoteDataSource: StudyPlanRemoteDataSource
): StudyPlanRepository {

    override suspend fun observeStudyPlan(id: String) = remoteDataSource.observeStudyPlan(id)
        .onStart {
            val localeStudyPlan = localDataSource.getStudyPlan(id)
            if (localeStudyPlan != null){
                emit(Result.success(localeStudyPlan))
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun getStudyPlan(id: String) = remoteDataSource.getStudyPlan(id)
        .onStart {
            val localeStudyPlan = localDataSource.getStudyPlan(id)
            if (localeStudyPlan != null)
                emit(Result.Success(localeStudyPlan))
        }.flowOn(Dispatchers.IO)

    override suspend fun getStudyPlans() = remoteDataSource.getStudyPlans()
        .onStart { refreshBreedsIfStale(true).collect(::emit) }
        .flowOn(Dispatchers.IO)

    override suspend fun deleteStudyPlan(id: String) = flow<Result<Unit>> {
        emit(Result.loading())
        remoteDataSource.deleteStudyPlan(id).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun createStudyPlan(studyPlan: StudyPlan) = flow<Result<Unit>> {
        emit(Result.loading())
        val simpleDateFormat= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentDT: String = simpleDateFormat.format(Date())
        val studyPlanDate = studyPlan.copy(updated = currentDT)
        remoteDataSource.createStudyPlan(studyPlanDate).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan) = flow<Result<Unit>> {
        emit(Result.loading())
        remoteDataSource.updateStudyPlan(id, studyPlan).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun updateStudyPlanFavorite(id: String, isFavorite: Boolean)  = flow<Unit> {
        localDataSource.updateStudyPlanFavorite(id, isFavorite)
    }.flowOn(Dispatchers.Default)

    fun refreshBreedsIfStale(forced: Boolean = false): Flow<Result<List<StudyPlan>>> = flow {
        emit(Result.loading())

        val localeStudyPlans = localDataSource.getStudyPlans()
        emit(Result.success(localeStudyPlans))

        if (forced) {
            val networkBreedDataState = remoteDataSource.getStudyPlans()
            networkBreedDataState.collect {
                when (it){
                    is Result.Failed -> emit(Result.failed<List<StudyPlan>>("Opps something went wrong!"))
                    is Result.Loading -> {}
                    is Result.Success -> it.data.forEach { studyPlan ->
                        localDataSource.createStudyPlan(studyPlan)
                    }
                }
            }
        }
        val newLocaleStudyPlans = localDataSource.getStudyPlans()
        emit(Result.success(newLocaleStudyPlans))
    }
}
