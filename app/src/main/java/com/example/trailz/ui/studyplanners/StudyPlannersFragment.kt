package com.example.trailz.ui.studyplanners

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.base.domain.Favorite
import com.example.base.domain.StudyPlan
import com.example.trailz.R
import com.example.trailz.inject.SharedPrefs
import com.example.trailz.ui.common.compose.FavoriteButton
import com.example.trailz.ui.studyplanner.StudyPlannerFragmentDirections
import com.google.firebase.database.collection.LLRBNode
import dagger.hilt.android.AndroidEntryPoint
import java.nio.file.Files.size
import javax.inject.Inject

@AndroidEntryPoint
class StudyPlannersFragment : Fragment() {

    private val viewModel: StudyPlannersViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.initObserveFavorites(sharedPrefs.loggedInId)
        return ComposeView(requireContext()).apply {
            setContent {
                StudyPlans(
                    viewModel = viewModel,
                    userId = sharedPrefs.loggedInId,
                    onStudyPlan = ::navigateToStudyPlan
                )
            }
        }
    }

    private fun navigateToStudyPlan(studyPlanId: String){
        val direction = StudyPlannersFragmentDirections.actionStudyPlannersToStudyPlanner(studyPlanId)
        findNavController().navigate(direction)
    }

    private fun setupClickListeners(profileBtn: View, studyPlannerBtn: View) {
        profileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_study_planners_to_profile)
        }

        studyPlannerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_study_planners_to_study_planner)
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun StudyPlans(
    viewModel: StudyPlannersViewModel,
    userId: String?,
    onStudyPlan: (String) -> Unit
) {
    val studyPlans by viewModel.studyPlans.observeAsState(initial = emptyList())
    val favorites by viewModel.favorite.observeAsState(initial = Favorite())

    StudyPlans(
        studyPlans = studyPlans,
        followedUserIds = favorites.followedUserIds,
        onFavorite = { viewModel.addToFavorite(it, userId) },
        onRemove = { viewModel.removeFromFavorite(it, userId) },
        onStudyPlan = onStudyPlan
    )
}

@ExperimentalMaterialApi
@Composable
fun StudyPlans(
    studyPlans: List<StudyPlan>,
    followedUserIds: List<String>,
    onFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
    onStudyPlan: (String) -> Unit
) {
    LazyColumn {
        items(studyPlans.size){ index ->
            val studyPlan = studyPlans[index]
            StudyPlan(
                studyPlan = studyPlan,
                checked = studyPlan.userId in followedUserIds,
                onFavorite = onFavorite,
                onRemove = onRemove,
                onStudyPlan = onStudyPlan
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun StudyPlan(
    studyPlan: StudyPlan,
    checked: Boolean,
    onFavorite: (String) -> Unit,
    onRemove: (String) -> Unit,
    onStudyPlan: (String) -> Unit
) {
    Card(
        onClick = { onStudyPlan(studyPlan.userId) }
    ) {
        Column {
            Text(text = studyPlan.title)
            Text(text = studyPlan.userId)
            Row {
                FavoriteButton(isChecked = checked) {
                    if (checked)
                        onRemove(studyPlan.userId)
                    else
                        onFavorite(studyPlan.userId)
                }
            }
        }
    }
}