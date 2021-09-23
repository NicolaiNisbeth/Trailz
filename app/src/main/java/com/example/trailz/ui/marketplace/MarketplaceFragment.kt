package com.example.trailz.ui.marketplace


import Market_place
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.google.android.material.composethemeadapter.MdcTheme

import android.os.Build
import android.util.AttributeSet
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@ExperimentalUnitApi
class MarketplaceFragment: Fragment() {
    private val viewModel: StudyPlansViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.getAllStudyPlans()
        return ComposeView(requireContext()).apply {
            setContent {
                MdcTheme{
                    Market_place(viewModel)
                }
            }
        }
    }
}

