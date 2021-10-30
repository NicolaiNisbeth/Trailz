package com.example.trailz.ui.studyplanners

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.*
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


@AndroidEntryPoint
class StudyPlannersFragment : Fragment() {

    private val viewModel: StudyPlanListViewModel by viewModels()
    private val adapter: StudyPlanListAdapter =
        StudyPlanListAdapter(onShippingProviderClicked = ::openStudyPlan)

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
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
                (view.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }
            }
        }
    }

    private fun setupShippingList(shippingList: RecyclerView) {
        shippingList.adapter = adapter
        shippingList.setHasFixedSize(true)
        shippingList.scheduleLayoutAnimation()

        val divider =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                val drawable = getDrawable(requireContext(), R.drawable.line)
                if (drawable != null) setDrawable(drawable)
            }
        shippingList.addItemDecoration(divider)
    }

    private fun openStudyPlan(view: StudyPlanlistBinding) {
        val extra =
            FragmentNavigatorExtras(view.imageView to "image_big", view.textView to "text_big")
        val args = Bundle().apply {
            putString("text_big", view.textView.text.toString())
        }
        findNavController().navigate(
            R.id.action_study_planners_to_study_planner,
            args,
            null,
            extra
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
}


class StudyPlanListAdapter(
    private val onShippingProviderClicked: (view: StudyPlanlistBinding) -> Unit
) : ListAdapter<StudyPlan, StudyPlanViewHolder>(IdEqualsDiffCallback { it.id }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyPlanViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return StudyPlanlistBinding
            .inflate(layoutInflater, parent, false)
            .run { StudyPlanViewHolder(this, onShippingProviderClicked) }
    }

    override fun onBindViewHolder(holder: StudyPlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class StudyPlan(val info: String, val id: Int)

class StudyPlanViewHolder(
    val binding: StudyPlanlistBinding,
    val onShippingProviderClicked: (view: StudyPlanlistBinding) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(provider: StudyPlan) {
        ViewCompat.setTransitionName(binding.textView, "text_small${provider.id}")
        ViewCompat.setTransitionName(binding.imageView, "image_small${provider.id}")
        binding.root.setOnClickListener {
            onShippingProviderClicked(binding)
        }
    }
}
