package com.example.trailz.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.trailz.*
import com.example.trailz.R
import com.example.trailz.language.LanguageConfig
import com.example.trailz.databinding.FragmentProfileBinding
import com.example.trailz.inject.SharedPrefs
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.platform.MaterialElevationScale
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
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
    private lateinit var onSettingsListener: OpenSettingsListener
    private lateinit var logoutListener: LogoutListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onLanguageListener = context as ChangeLanguageListener
            onSettingsListener = context as OpenSettingsListener
            logoutListener = context as LogoutListener
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
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        setupComposeView(binding.composeViewProfile)
        return binding.root
    }

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    private fun setupComposeView(composeViewProfile: ComposeView) {
        composeViewProfile.setContent {
            MdcTheme {
                Profile(
                    viewModel = viewModel,
                    appliedCountry = appliedCountry,
                    isDarkTheme = sharedPrefs.isDarkTheme,
                    rateApp = ::openGooglePlay,
                    toggleTheme = ::toggleTheme,
                    onChangeLanguage = onLanguageListener::onChangeLanguage,
                    settings = onSettingsListener::onOpenSettingsListener,
                    navigateUp = findNavController()::navigateUp,
                    logout = logoutListener::onLogout
                )
            }
        }
    }

    private fun toggleTheme(isDarkTheme: Boolean){
        sharedPrefs.isDarkTheme = isDarkTheme
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun openGooglePlay(){
        Toast.makeText(requireContext(), "Not available yet...", Toast.LENGTH_SHORT).show()
    }
}