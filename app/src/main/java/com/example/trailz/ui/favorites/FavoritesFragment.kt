package com.example.trailz.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import com.example.trailz.databinding.FragmentFavoritesBinding
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPrefs.loggedInId?.let { viewModel.initObserveFavoriteBy(it) }
        return FragmentFavoritesBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .also { setupClickListeners(it.profileBtn, it.studyPlannerBtn) }
            .run { root }
    }

    private fun setupClickListeners(profileBtn: View, studyPlannerBtn: View) {
        profileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_favorites_to_profile)
        }

        studyPlannerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_favorites_to_study_planner)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.favorite.observe(viewLifecycleOwner){
            binding.textDashboard.text = it.followedUserIds.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}