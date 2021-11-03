package com.example.trailz.ui.signup

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
import com.example.trailz.R
import com.example.trailz.databinding.FragmentSignUpBinding
import com.example.trailz.ui.login.LoginListener
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment: Fragment() {

    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
    }

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
    @ExperimentalPagerApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)
        setupSignUpComposable(binding.composeViewSignUp)
        return binding.root
    }

    @ExperimentalComposeUiApi
    @ExperimentalPagerApi
    private fun setupSignUpComposable(composeView: ComposeView) {
        composeView.setContent {
            MdcTheme {
                SignUp(
                    viewModel = viewModel,
                    navigateUp = findNavController()::navigateUp,
                    popLogin = loginListener::onLoginSuccess
                )
            }
        }
    }
}