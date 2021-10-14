package com.example.trailz.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.trailz.ChangeLanguageListener
import com.example.trailz.language.LanguageConfig
import com.example.trailz.R
import com.example.trailz.databinding.FragmentProfileBinding
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val appliedCountry by lazy {
        LanguageConfig.languageToConfig(sharedPrefs.languagePreference)
    }

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var onLanguageListener: ChangeLanguageListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onLanguageListener = context as ChangeLanguageListener
        } catch (e: Error) {
            throw IllegalStateException("Activity must implement $onLanguageListener")
        }
    }

    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.getUser(sharedPrefs.loggedInId)
        return ComposeView(requireContext()).apply {
            setContent {
                Profile(
                    viewModel = viewModel,
                    appliedCountry = appliedCountry,
                    onChangeLanguage = { onLanguageListener.onChangeLanguage(it) },
                    navigateUp = { findNavController().navigateUp() },
                    signIn = ::signIn,
                    signUp = ::signUp,
                    rateApp = ::openGooglePlay
                )
            }
        }
    }

    private fun openGooglePlay(){
        Toast.makeText(requireContext(), "Not available yet...", Toast.LENGTH_SHORT).show()
    }

    private fun signIn(){
        findNavController().navigate(R.id.action_profile_to_signin)
    }

    private fun signUp(){
        findNavController().navigate(R.id.action_profile_to_signup)
    }
}