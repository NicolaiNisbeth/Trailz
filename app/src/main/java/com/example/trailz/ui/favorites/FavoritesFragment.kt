package com.example.trailz.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import com.example.trailz.databinding.FragmentFavoritesBinding
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.studyplanners.StudyPlannersFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPrefs.loggedInId?.let { viewModel.initObserveFavoriteBy(it) }
        return ComposeView(requireContext()).apply {
            setContent {
                Favorites(
                    viewModel = viewModel,
                    userId = sharedPrefs.loggedInId,
                    onStudyPlan = ::openStudyPlan,
                    onProfile = ::openProfile
                )
            }
        }
    }

    private fun openStudyPlan(ownerId: String){
        val direction = FavoritesFragmentDirections.actionFavoritesToStudyPlanner(ownerId)
        findNavController().navigate(direction)
    }

    private fun openProfile(){
        findNavController().navigate(R.id.action_favorites_to_profile)
    }
}