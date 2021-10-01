package com.example.trailz.ui.studyplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.trailz.R
import com.example.trailz.StudyPlannerNavigationArgs
import com.example.trailz.ui.studyplanners.StudyPlannersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyPlannerFragment: Fragment() {

    private val viewModel: StudyPlannerViewModel by viewModels()

    private val args: StudyPlannerNavigationArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.test(args.ownerId)
        return ComposeView(requireContext()).apply {
            setContent {
                StudyPlanner(
                    openMarketplace = {
                        findNavController().navigate(R.id.action_study_planner_to_marketplace)
                    }
                )
            }
        }
    }
}