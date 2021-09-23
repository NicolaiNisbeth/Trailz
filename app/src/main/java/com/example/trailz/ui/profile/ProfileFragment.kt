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
import com.example.trailz.ui.signup.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private val listener = object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            binding.textNotifications.text = snapshot.value.toString()
        }

        override fun onCancelled(error: DatabaseError) {
            binding.textNotifications.text = error.message
        }
    }

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
        viewModel.userId.observe(viewLifecycleOwner){ userId ->
            if (userId != null){
                viewModel.getUser(userId, listener)
                binding.loginButton.visibility = View.GONE
                binding.signupButton.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners(loginButton: View, signupButton: View) {
        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_signin)
        }

        signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_signup)
        }
    }
}