package com.example.trailz.ui.studyplanners

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudyPlannersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is studyPlanners Fragment"
    }
    val text: LiveData<String> = _text
}