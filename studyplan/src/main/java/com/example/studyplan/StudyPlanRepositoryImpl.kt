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

    override suspend fun observeStudyPlan(id: String): Flow<Result<StudyPlan>> = flow {
        remoteDataSource.observeStudyPlan(id).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun getStudyPlan(id: String): Flow<Result<StudyPlan>> = flow {
        emit(Result.loading())
        remoteDataSource.getStudyPlan(id).collect(::emit)
    }.flowOn(Dispatchers.IO)

    @FlowPreview
    override suspend fun getStudyPlans() = remoteDataSource.getStudyPlans()

    @FlowPreview
    override suspend fun observeStudyPlans() = flow {
        emit(Result.loading())
        remoteDataSource.observeStudyPlans().collect(::emit)
    }

    fun refreshBreedsIfStale(forced: Boolean = false): Flow<Result<List<StudyPlan>>> = flow {
        emit(Result.loading())
        val networkBreedDataState: Flow<Result<List<StudyPlan>>>
        if (forced) {
            networkBreedDataState = remoteDataSource.observeStudyPlans()
            networkBreedDataState.collect {
                if (it is Result.Success){
                    it.data.forEach { localDataSource.createStudyPlan(it) }
                }
            }
        }
    }

    private suspend fun getStudyPlansFromCache(): Flow<Result<List<StudyPlan>>> = flow {
        emit(Result.success(localDataSource.getStudyPlans().sortedBy { it.title }))
    }.flowOn(Dispatchers.IO)


    override suspend fun deleteStudyPlan(id: String) = flow<Result<Unit>> {
        emit(Result.loading())
        remoteDataSource.deleteStudyPlan(id).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun createStudyPlan(studyPlan: StudyPlan) = flow<Result<Unit>> {
        val simpleDateFormat= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentDT: String = simpleDateFormat.format(Date())

        val studyPlanDate = studyPlan.copy(updated = currentDT)
        emit(Result.loading())
        remoteDataSource.createStudyPlan(studyPlanDate).collect(::emit)
    }.flowOn(Dispatchers.IO)

    override suspend fun updateStudyPlan(id: String, studyPlan: StudyPlan) = flow<Result<Unit>> {
        emit(Result.loading())
        remoteDataSource.updateStudyPlan(id, studyPlan).collect(::emit)
    }.flowOn(Dispatchers.IO)
}
