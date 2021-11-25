package com.dtu.trailz.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dtu.trailz.R
import com.dtu.trailz.databinding.FragmentFavoritesBinding
import com.dtu.trailz.inject.SharedPrefs
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        setupComposeView(binding.composeViewFavorites)
        return binding.root
    }

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    private fun setupComposeView(composeViewFavorites: ComposeView) {
        composeViewFavorites.setContent {
            MdcTheme {
                Favorites(
                    viewModel = viewModel,
                    onStudyPlan = ::openStudyPlan,
                    onProfile = ::openProfile,
                    onFindFavorite = ::openStudyPlanners
                )
            }
        }
    }

    private fun openStudyPlan(ownerId: String){
        if (ownerId == sharedPrefs.loggedInId){
            findNavController().navigate(R.id.action_favorites_to_my_study_plan)
        } else {
            val direction = FavoritesFragmentDirections.actionFavoritesToStudyPlan(ownerId)
            findNavController().navigate(direction)
        }
    }

    private fun openProfile(){
        findNavController().navigate(R.id.action_favorites_to_profile)
    }

    private fun openStudyPlanners(){
        findNavController().navigate(R.id.action_favorites_to_study_plans)
    }
}