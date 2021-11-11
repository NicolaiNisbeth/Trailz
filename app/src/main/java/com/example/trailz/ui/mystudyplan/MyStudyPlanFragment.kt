package com.example.trailz.ui.mystudyplan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.ChangeAnimationListener
import com.example.trailz.ChangeLanguageListener
import com.example.trailz.OpenSettingsListener
import com.example.trailz.R
import com.example.trailz.databinding.FragmentMyStudyPlanBinding
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class MyStudyPlanFragment: Fragment() {

    private val viewModel: MyStudyPlanViewModel by viewModels()

    private lateinit var changeAnimationListener: ChangeAnimationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            changeAnimationListener = context as ChangeAnimationListener
        } catch (e: Error) {
            throw IllegalStateException("Activity must implement $changeAnimationListener")
        }
    }

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