package com.example.trailz.ui.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.trailz.R


class SignInFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Login(
                    onLoginSuccess = findNavController()::navigateUp,
                    onSignUp = { findNavController().navigate(R.id.action_signin_to_signup) }
                )
            }
        }
    }
}