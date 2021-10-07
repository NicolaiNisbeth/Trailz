package com.example.trailz.ui.mystudyplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import com.example.trailz.ui.studyplanners.StudyPlan
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyStudyPlanFragment: Fragment() {

    private val viewModel: MyStudyPlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MyStudyPlan(
                    viewModel = viewModel,
                    onProfile = ::openProfile
                )
            }
        }
    }

    private fun openProfile() {
        findNavController().navigate(R.id.action_my_study_plan_to_profile)
    }
}

@Composable
fun MyStudyPlan(
    viewModel: MyStudyPlanViewModel,
    onProfile: () -> Unit
) {
    MyStudyPlan(
        onProfile = onProfile
    )
}

@Composable
fun MyStudyPlan(
    onProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Plan") },
                backgroundColor = MaterialTheme.colors.background,
                actions = {
                    IconButton(onClick = onProfile) {
                        Icon(imageVector = Icons.Default.PermIdentity, contentDescription = null)
                    }
                }
            )
        }
    ) {
        // content here
    }
}