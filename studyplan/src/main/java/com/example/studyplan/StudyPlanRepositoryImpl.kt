package com.example.studyplan

import com.example.base.domain.StudyPlan
import com.example.base.Result
import com.example.studyplan.local.StudyPlanLocalDataSource
import com.example.studyplan.remote.StudyPlanRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class StudyPlanRepositoryImpl(
    private val localDataSource: StudyPlanLocalDataSource,
    private val remoteDataSource: StudyPlanRemoteDataSource
): StudyPlanRepository {

    override suspend fun getStudyPlan(id: String?): Flow<Result<StudyPlan>> = if (id != null){
        remoteDataSource.getStudyPlan(id)
    } else {
        flow {
            val studyPlan = localDataSource.getStudyPlan()
            if (studyPlan != null){
                emit(Result.success<StudyPlan>(studyPlan))
            } else {
                emit(Result.failed<StudyPlan>(message = "user with id=$id has no studyplan"))
            }
        }
    }.flowOn(Dispatchers.IO)

    @FlowPreview
    override suspend fun getStudyPlans() = observeStudyPlans()

    fun refreshBreedsIfStale(forced: Boolean = false): Flow<Result<List<StudyPlan>>> = flow {
        emit(Result.loading())
        val networkBreedDataState: Flow<Result<List<StudyPlan>>>
        if (forced) {
            networkBreedDataState = remoteDataSource.getStudyPlans()
            networkBreedDataState.collect {
                if (it is Result.Success){
                    it.data.forEach { localDataSource.createStudyPlan(it) }
                } else {
                    networkBreedDataState.collect { emit(it) }
                }
            }
        }
    }

    private suspend fun getStudyPlansFromCache(): Flow<Result<List<StudyPlan>>> =
        localDataSource.observeStudyPlans()
            .mapNotNull {
                if (it.isEmpty()){
                    null
                } else {
                    Result.success(it)
                }
            }

    @FlowPreview
    override suspend fun observeStudyPlans() = flowOf(
        refreshBreedsIfStale(true),
        getStudyPlansFromCache()
    ).flattenMerge().flowOn(Dispatchers.IO)

    override suspend fun deleteStudyPlan(id: String) = flow<Result<Unit>> {
        localDataSource.deleteStudyPlan(id)
        remoteDataSource.deleteStudyPlan(id).collect {
            if (it is Result.Success){
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createStudyPlan(studyPlan: StudyPlan) = flow<Result<Unit>> {
        localDataSource.createStudyPlan(studyPlan)
        remoteDataSource.createStudyPlan(studyPlan).collect {
            if (it is Result.Success){
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan) = flow<Result<Unit>> {
        localDataSource.updateStudyPlan(id, studyPlan)
        remoteDataSource.updateStudyPlan(id, studyPlan).collect {
            if (it is Result.Success){
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)
}
