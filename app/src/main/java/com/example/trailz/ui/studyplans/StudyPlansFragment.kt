package com.example.trailz.ui.studyplans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import dagger.hilt.android.AndroidEntryPoint
import com.example.trailz.databinding.FragmentStudyPlansBinding
import com.example.trailz.inject.SharedPrefs
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class StudyPlansFragment : Fragment() {

    @Inject
    lateinit var prefs: SharedPrefs

    lateinit var binding: FragmentStudyPlansBinding
    private val viewModel: StudyPlansViewModel by viewModels()

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyPlansBinding.inflate(inflater, container, false)
        setupComposeView(binding.composeViewStudyPlans)
        return binding.root
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    private fun setupComposeView(composeViewStudyPlans: ComposeView) {
        composeViewStudyPlans.setContent {
            MdcTheme {
                StudyPlans(
                    viewModel = viewModel,
                    onProfile = ::openProfile,
                    onStudyPlan = ::openStudyPlan
                )
            }
        }
    }

    private fun openProfile() {
        findNavController().navigate(R.id.action_study_plans_to_profile)
    }

    private fun openStudyPlan(userId: String) {
        if (userId == prefs.loggedInId){
            findNavController().navigate(R.id.action_study_plans_to_my_study_plan)
        } else {
            findNavController().navigate(
                resId = R.id.action_study_plans_to_study_plan,
                args = Bundle().apply { putString("ownerId", userId) },
                navOptions = null,
            )
        }
    }
}
