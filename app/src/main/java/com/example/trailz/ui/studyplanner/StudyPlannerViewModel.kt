package com.example.trailz.ui.studyplanner

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudyPlannerViewModel @Inject constructor(): ViewModel() {



    fun test(ownerId: String){
        Log.d("hej", ownerId)
    }
}