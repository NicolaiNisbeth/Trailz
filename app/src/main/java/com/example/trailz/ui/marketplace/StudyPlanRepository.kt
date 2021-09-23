package com.example.trailz.ui.marketplace

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

data class StudyPlan(
    val owner: String = "tom",
    val titel: String = "tomstreng",
)

class GetStudyPlanUseCase(
    private val repository: StudyPLansRepository
){
    suspend operator fun invoke(email: String, listener: ValueEventListener) = withContext(
        Dispatchers.IO){
        repository.getStudyPlan(email, listener)
    }
}
class GetAllStudyPlansUseCase(
    private val repository: StudyPLansRepository
){
    suspend operator fun invoke(listener: ValueEventListener) = withContext(
        Dispatchers.IO){
        repository.getAllStudyPlans(listener)
    }
}


interface StudyPLansRepository {
    fun getStudyPlan(email: String, listener: ValueEventListener): String?
    fun getAllStudyPlans(listener: ValueEventListener)
}

class StudyPlanRepositoryImpl(
    private val studyPlanService: StudyPlanService
): StudyPLansRepository {

    override fun getStudyPlan(
        email: String,
        listener: ValueEventListener
    ) = studyPlanService.getStudyPlan(email, listener)

    override fun getAllStudyPlans(
        listener: ValueEventListener
    ) = studyPlanService.getAllStudyPlans(listener)
}

interface StudyPlanService {
    fun getStudyPlan(plan: String, listener: ValueEventListener):String?
    fun getAllStudyPlans(listener: ValueEventListener)
}

class StudyPlanFirebase(
    private val databaseReference: FirebaseDatabase
): StudyPlanService {

    override fun getStudyPlan(plan: String, listener: ValueEventListener): String? {
        val key = databaseReference.getReference("studyplans").push()
        key.setValue(plan)
        return key.key
    }

    override fun getAllStudyPlans(listener: ValueEventListener) {
        databaseReference.getReference("studyplans")
            .addListenerForSingleValueEvent(listener)
    }

}

