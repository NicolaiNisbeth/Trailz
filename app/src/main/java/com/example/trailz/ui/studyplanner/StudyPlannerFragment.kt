package com.example.trailz.ui.studyplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.trailz.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyPlannerFragment: Fragment() {

    private val viewModel: StudyPlannerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                StudyPlan(
                    viewModel = viewModel,
                    navigateUp = findNavController()::navigateUp
                )
            }
        }
    }
}