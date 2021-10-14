package com.example.trailz.ui.favorites

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.ChangeAnimationListener
import com.example.trailz.R
import com.example.trailz.databinding.FragmentFavoritesBinding
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.studyplanners.StudyPlannersFragmentDirections
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalStateException
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private lateinit var changeAnimationListener: ChangeAnimationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            changeAnimationListener = context as ChangeAnimationListener
        } catch (e: Error) {
            throw IllegalStateException("Activity must implement $changeAnimationListener")
        }
    }

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPrefs.loggedInId?.let { viewModel.initObserveFavoriteBy(it) }
        val binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        setupComposeView(binding.composeViewFavorites)
        return binding.root
    }

    @ExperimentalMaterialApi
    private fun setupComposeView(composeViewFavorites: ComposeView) {
        composeViewFavorites.setContent {
            MdcTheme {
                Favorites(
                    viewModel = viewModel,
                    userId = sharedPrefs.loggedInId,
                    onStudyPlan = ::openStudyPlan,
                    onProfile = ::openProfile,
                    onFindFavorite = ::openStudyPlanners
                )
            }
        }
    }

    private fun openStudyPlan(ownerId: String){
        val direction = FavoritesFragmentDirections.actionFavoritesToStudyPlanner(ownerId)
        findNavController().navigate(direction)
    }

    private fun openProfile(){
        changeAnimationListener.applyAnimationChanges {
            exitTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
            }
        }
        findNavController().navigate(R.id.action_favorites_to_profile)
    }

    private fun openStudyPlanners(){
        findNavController().navigate(R.id.action_favorites_to_study_planners)
    }
}