package com.example.trailz.ui.studyplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import com.example.trailz.databinding.FragmentStudyPlanBinding
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyPlanFragment : Fragment() {

    private val viewModel: StudyPlanViewModel by viewModels()
    lateinit var binding: FragmentStudyPlanBinding

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStudyPlanBinding.inflate(inflater, container, false)
        setupComposeView(binding.composeViewStudyPlan)
        return binding.root
    }

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    private fun setupComposeView(composeViewMyStudyPlan: ComposeView) {
        composeViewMyStudyPlan.setContent {
            MdcTheme {
                StudyPlan(
                    viewModel = viewModel,
                    navigateUp = { findNavController().navigateUp() },
                    onProfile = ::openProfile
                )
            }
        }
    }

    private fun openProfile() {
        findNavController().navigate(R.id.action_study_plan_to_profile)
    }
}
