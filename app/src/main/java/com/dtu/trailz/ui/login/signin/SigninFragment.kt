package com.dtu.trailz.ui.login.signin

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dtu.trailz.R
import com.dtu.trailz.databinding.FragmentSignInBinding
import com.dtu.trailz.ui.login.LoginListener
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SigninFragment: Fragment() {

    private val viewModel: SigninViewModel by viewModels()

    private lateinit var loginListener: LoginListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            loginListener = context as LoginListener
        } catch (e : Exception){
            throw IllegalStateException("Activity must implement $loginListener")
        }
    }

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        setupSignInComposable(binding.composeViewSignIn)
        return binding.root
    }

    @ExperimentalComposeUiApi
    private fun setupSignInComposable(composeView: ComposeView) {
        composeView.setContent {
            MdcTheme {
                SignIn(
                    viewModel = viewModel,
                    onNavigateUp = loginListener::onLoginSuccess,
                    onSignUp = { findNavController().navigate(R.id.action_signin_to_signup) }
                )
            }
        }
    }
}