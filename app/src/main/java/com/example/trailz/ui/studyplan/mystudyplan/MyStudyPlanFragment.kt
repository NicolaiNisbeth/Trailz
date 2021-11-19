package com.example.trailz.ui.studyplan.mystudyplan

import android.content.Context
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
import com.example.trailz.databinding.FragmentMyStudyPlanBinding
import com.example.trailz.ui.studyplan.MyStudyPlanViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class MyStudyPlanFragment: Fragment() {

    private val viewModel: MyStudyPlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyStudyPlanBinding.inflate(inflater, container, false)
        setupComposeView(binding.composeViewMyStudyPlan)
        return binding.root
    }

    private fun setupComposeView(composeViewMyStudyPlan: ComposeView) {
        composeViewMyStudyPlan.setContent {
            MdcTheme {
                MyStudyPlan(
                    viewModel = viewModel,
                    onProfile = ::openProfile,
                    navigateUp = findNavController()::navigateUp
                )
            }
        }
    }

    private fun openProfile() {
        findNavController().navigate(R.id.action_my_study_plan_to_profile)
    }
}