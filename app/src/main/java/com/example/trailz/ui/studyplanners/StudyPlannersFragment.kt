package com.example.trailz.ui.studyplanners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.example.trailz.R
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.base.Result
import com.example.base.domain.StudyPlan
import com.example.favorite.FavoriteRepository
import com.example.studyplan.StudyPlanRepository
import com.example.trailz.databinding.FragmentStudyPlannersBinding
import com.example.trailz.databinding.StudyPlanlistBinding
import com.example.trailz.ui.common.IdEqualsDiffCallback
import com.example.trailz.ui.common.compose.FavoriteButton
import com.example.trailz.ui.common.themeColor
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudyPlannersFragment : Fragment() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var binding: FragmentStudyPlannersBinding
    private val viewModel: StudyPlanListViewModel by viewModels()
    private val adapter: StudyPlanListAdapter by lazy {
        StudyPlanListAdapter(
            onShippingProviderClicked = ::openStudyPlan,
            onFavoriteClicked = { id, isChecked ->
                viewModel.updateChecked(id, isChecked)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = TransitionInflater
            .from(requireContext())
            .inflateTransition(R.transition.fade)
    }

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (viewModel.studyPlansState.value.data == null){
            viewModel.observeStudyPlans()
        }
        binding = FragmentStudyPlannersBinding.inflate(inflater, container, false)
        return binding.also {
            setupShippingList(it.recyclerView)
            setupToolbar(it.toolbarView)
        }.root
    }

    private fun setupToolbar(toolbarView: ComposeView) {
        toolbarView.setContent {
            MdcTheme {
                TopAppBar(
                    title = { Text(text = "Study Plans") },
                    backgroundColor = MaterialTheme.colors.background,
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        lifecycleScope.launch {
            viewModel.studyPlansState.collect {
                handleData(it.data)
                handleLoading(it.isLoading)
                handleEmpty(it.isEmpty)
                handleError(it.exception)
            }
        }

    }

    private fun handleError(exception: String?) {
        exception?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleEmpty(empty: Boolean) {
        if (empty) {
            Toast.makeText(context, "empty screen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun handleData(studyPlans: List<StudyPlan>?) {
        adapter.submitList(studyPlans) {
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }
    }

    private fun setupShippingList(shippingList: RecyclerView) {
        shippingList.adapter = adapter
        shippingList.itemAnimator = null
        shippingList.setHasFixedSize(true)
        shippingList.scheduleLayoutAnimation()
        shippingList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                val drawable = getDrawable(requireContext(), R.drawable.line)
                if (drawable != null) setDrawable(drawable)
            }
        )
    }

    private fun openStudyPlan(view: StudyPlanlistBinding) {
        val args = Bundle().apply {
            putInt("image_big", R.drawable.ic_launcher_foreground) // FIXME: cheating
            putString("text_big", view.textView.text.toString())
            putString("ownerId", view.authorView.text.toString())
        }
        val extra = FragmentNavigatorExtras(
            view.imageView to "image_big",
            view.textView to "text_big"
        )
        findNavController().navigate(
            resId = R.id.action_study_planners_to_study_planner,
            args = args,
            navOptions = null,
            navigatorExtras = extra
        )
    }
}

data class DataState<out T>(
    val data: T? = null,
    val exception: String? = null,
    val isEmpty: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class StudyPlanListViewModel @Inject constructor(
    private val studyPlanRepository: StudyPlanRepository,
    private val favoriteRepository: FavoriteRepository,
    private val sharedPrefs: SharedPrefs,
) : ViewModel() {

    private val scope = viewModelScope
    private val _studyPlansState: MutableStateFlow<DataState<List<StudyPlan>>> = MutableStateFlow(
        DataState(isLoading = true)
    )
    val studyPlansState: MutableStateFlow<DataState<List<StudyPlan>>> = _studyPlansState

    fun observeStudyPlans() {
        scope.launch {
            val studyPlansFlow = studyPlanRepository.getStudyPlans()
            val favoritesFlow = favoriteRepository.getFavoritesBy(sharedPrefs.loggedInId)
            studyPlansFlow.combine(favoritesFlow){ studyPlansRes, favoritesRes ->
                if (studyPlansRes is Result.Loading || favoritesRes is Result.Loading){
                    _studyPlansState.value = _studyPlansState.value.copy(isLoading = true)
                } else if (studyPlansRes is Result.Failed){
                    _studyPlansState.value = _studyPlansState.value.copy(exception = "failed loading study plans")
                } else if(studyPlansRes is Result.Success && favoritesRes is Result.Success){
                    val favorites = favoritesRes.data.followedUserIds.toHashSet()
                    val studyPlans = studyPlansRes.data.map {
                        it.copy(isChecked = favorites.contains(it.userId))
                    }
                    _studyPlansState.value = DataState(studyPlans, isEmpty = studyPlans.isEmpty())

                } else if (studyPlansRes is Result.Success && favoritesRes is Result.Failed){
                    val studyPlans = studyPlansRes.data
                    _studyPlansState.value = DataState(studyPlans, isEmpty = studyPlans.isEmpty())
                }
                else {
                    _studyPlansState.value = _studyPlansState.value.copy(isEmpty = true)
                }
            }.conflate().collect()
        }
    }

    fun updateChecked(studyPlanId: String, isChecked: Boolean) {
        scope.launch {
            flipLocally(studyPlanId, isChecked)
            val userId = sharedPrefs.loggedInId
            val flow = if (isChecked) favoriteRepository.removeFromFavorite(studyPlanId, userId)
            else favoriteRepository.addToFavorite(studyPlanId, userId)
            flow.collect()
        }
    }

    private fun flipLocally(studyPlanId: String, checked: Boolean) {
        _studyPlansState.value = _studyPlansState.value.copy(
            data = _studyPlansState.value.data?.run {
                map {
                    if (it.userId == studyPlanId) it.copy(isChecked = checked.not(), likes = if (checked) it.likes.minus(1) else it.likes.plus(1) )
                    else it
                }
            }
        )
    }
}


class StudyPlanListAdapter(
    private val onShippingProviderClicked: (view: StudyPlanlistBinding) -> Unit,
    val onFavoriteClicked: (String, Boolean) -> Unit
) : ListAdapter<StudyPlan, StudyPlanViewHolder>(IdEqualsDiffCallback { it.userId }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyPlanViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return StudyPlanlistBinding
            .inflate(layoutInflater, parent, false)
            .run { StudyPlanViewHolder(this, onShippingProviderClicked, onFavoriteClicked) }
    }

    override fun onBindViewHolder(holder: StudyPlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class StudyPlanViewHolder(
    val binding: StudyPlanlistBinding,
    val onShippingProviderClicked: (view: StudyPlanlistBinding) -> Unit,
    val onFavoriteClicked: (String, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val colorChecked = binding.root.context.themeColor(R.attr.colorPrimary)
    private val colorUnchecked = binding.root.context.themeColor(R.attr.colorOnBackground)

    fun bind(studyPlan: StudyPlan) {
        binding.root.setOnClickListener {
            onShippingProviderClicked(binding)
        }

        binding.textView.text = studyPlan.title
        binding.authorView.text = studyPlan.username
        binding.updatedView.text = studyPlan.updated
        binding.likesView.text = "${studyPlan.likes} likes"
        binding.likesBtn.setContent {
            FavoriteButton(
                isChecked = studyPlan.isChecked,
                colorOnChecked = Color(colorChecked),
                colorUnChecked = Color(colorUnchecked)
            ) {
                onFavoriteClicked(studyPlan.userId, studyPlan.isChecked)
            }
        }

        ViewCompat.setTransitionName(binding.textView, "text_small${studyPlan.userId}")
        ViewCompat.setTransitionName(binding.imageView, "image_small${studyPlan.userId}")
    }
}
