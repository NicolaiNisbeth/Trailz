package com.example.trailz.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import com.example.trailz.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentProfileBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .also { setupClickListeners(it.loginButton, it.signupButton) }
            .run { root }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.text.observe(viewLifecycleOwner){
            binding.textNotifications.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners(loginButton: View, signupButton: View) {
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_login)
        }

        signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_signup)
        }
    }
}