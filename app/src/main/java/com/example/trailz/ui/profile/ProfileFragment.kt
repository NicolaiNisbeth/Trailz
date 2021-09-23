package com.example.trailz.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import com.example.trailz.databinding.FragmentProfileBinding
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.getUser()
        return FragmentProfileBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .also { setupClickListeners(it.loginButton, it.signupButton, it.logoutButton) }
            .run { root }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.state.observe(viewLifecycleOwner) {
                binding.progressCircular.visibility = if (it.isLoading) View.VISIBLE else View.GONE
                binding.signupButton.visibility = if (!it.isLoggedIn) View.VISIBLE else View.GONE
                binding.loginButton.visibility = if (!it.isLoggedIn) View.VISIBLE else View.GONE
                binding.logoutButton.visibility = if (it.isLoggedIn) View.VISIBLE else View.GONE
                binding.textNotifications.text = it.user.toString()
                it.error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners(loginButton: View, signupButton: View, logoutButton: View) {
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_signin)
        }

        signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_signup)
        }
        logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }
}