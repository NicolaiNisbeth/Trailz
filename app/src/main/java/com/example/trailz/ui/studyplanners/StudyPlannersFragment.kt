package com.example.trailz.ui.studyplanners

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.example.trailz.R
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trailz.databinding.FragmentStudyPlannersBinding
import com.example.trailz.databinding.StudyPlanlistBinding
import com.example.trailz.ui.common.IdEqualsDiffCallback
import com.example.trailz.ui.common.compose.FavoriteButton
import com.example.trailz.ui.common.themeColor


@AndroidEntryPoint
class StudyPlannersFragment : Fragment() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val viewModel: StudyPlanListViewModel by viewModels()
    private val adapter: StudyPlanListAdapter =
        StudyPlanListAdapter(
            onShippingProviderClicked = ::openStudyPlan,
            onFavoriteClicked = { id, isChecked -> viewModel.updateChecked(id, isChecked) }
        )

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
        return FragmentStudyPlannersBinding.inflate(inflater, container, false)
            .also { setupShippingList(it.recyclerView) }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        viewModel.shippingProvider.observe(viewLifecycleOwner) {
            adapter.submitList(it){
                (view.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }
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


@HiltViewModel
class StudyPlanListViewModel @Inject constructor(
) : ViewModel() {
    val shippingProvider = MutableLiveData(
        listOf(
            StudyPlan("1", 0),
            StudyPlan("2", 1),
            StudyPlan("3", 2),
            StudyPlan("4", 3),
            StudyPlan("5", 4),
            StudyPlan("6", 5),
            StudyPlan("7", 6),
            StudyPlan("8", 7),
            StudyPlan("9", 8),
            StudyPlan("10", 9),
            StudyPlan("11", 10),
            StudyPlan("12", 11),
            StudyPlan("13", 12)
        )
    )

    fun updateChecked(id: Int, setChecked: Boolean){
        shippingProvider.value = shippingProvider.value?.map {
            if (it.id == id) it.copy(isChecked = setChecked)
            else it
        }
    }
}


class StudyPlanListAdapter(
    private val onShippingProviderClicked: (view: StudyPlanlistBinding) -> Unit,
    val onFavoriteClicked: (Int, Boolean) -> Unit
) : ListAdapter<StudyPlan, StudyPlanViewHolder>(IdEqualsDiffCallback { it.id }) {

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

data class StudyPlan(val info: String, val id: Int, val isChecked: Boolean = false)

class StudyPlanViewHolder(
    val binding: StudyPlanlistBinding,
    val onShippingProviderClicked: (view: StudyPlanlistBinding) -> Unit,
    val onFavoriteClicked: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val colorChecked = binding.root.context.themeColor(R.attr.colorPrimary)
    private val colorUnchecked = binding.root.context.themeColor(R.attr.colorOnBackground)

    fun bind(provider: StudyPlan) {
        ViewCompat.setTransitionName(binding.textView, "text_small${provider.id}")
        ViewCompat.setTransitionName(binding.imageView, "image_small${provider.id}")
        binding.root.setOnClickListener {
            onShippingProviderClicked(binding)
        }
        binding.likesBtn.setContent {
            FavoriteButton(
                isChecked = provider.isChecked,
                colorOnChecked = Color(colorChecked),
                colorUnChecked = Color(colorUnchecked)
            ) {
                onFavoriteClicked(provider.id, !it)
            }
        }
    }
}
