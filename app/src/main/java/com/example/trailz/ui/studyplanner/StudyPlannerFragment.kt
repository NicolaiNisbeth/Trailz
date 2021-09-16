package com.example.trailz.ui.studyplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.trailz.R

class StudyPlannerFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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