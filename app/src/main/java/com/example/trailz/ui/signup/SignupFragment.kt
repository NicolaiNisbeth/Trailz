package com.example.trailz.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.accompanist.pager.ExperimentalPagerApi

class SignupFragment: Fragment() {

    private val viewModel: SignupViewModel by viewModels()

    @ExperimentalPagerApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Signup(
                    viewModel = viewModel,
                    onSignupSuccess = findNavController()::navigateUp,
                )
            }
        }
    }
}