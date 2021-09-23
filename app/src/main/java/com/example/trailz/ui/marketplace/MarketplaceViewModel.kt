package com.example.trailz.ui.marketplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StudyPlansViewModel @Inject constructor(
    private val getAllStudyPlansUseCase: GetAllStudyPlansUseCase
) : ViewModel() {

    private val _studyplans = MutableLiveData<List<StudyPlan>>()
    val studyplans: LiveData<List<StudyPlan>> = _studyplans

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun getAllStudyPlans(){

        viewModelScope.launch {
            _loading.value = true
            getAllStudyPlansUseCase(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    _studyplans.value = snapshot.children.mapNotNull {
                        //StudyPlan(it.value.toString(),it.value.toString())
                        it.getValue(StudyPlan::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
            _loading.value = false
        }

    }
}

