package com.example.trailz.ui.studyplanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.transition.TransitionInflater
import com.example.trailz.R
import com.example.trailz.databinding.FragmentMyStudyPlanBinding
import com.example.trailz.databinding.FragmentStudyplanLargeBinding
import com.example.trailz.databinding.SemesterBinding
import com.example.trailz.ui.common.IdEqualsDiffCallback
import com.example.trailz.ui.mystudyplan.MyStudyPlan
import com.example.trailz.ui.mystudyplan.MyStudyPlanViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@AndroidEntryPoint
class StudyPlannerFragment : Fragment() {

    private val viewModel: MyStudyPlanViewModel by viewModels()
    lateinit var binding: FragmentStudyplanLargeBinding
    private val adapter: StudyPlannerListAdapter = StudyPlannerListAdapter {}

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMyStudyPlanBinding.inflate(inflater, container, false)
        setupComposeView(binding.composeViewMyStudyPlan)
        return binding.root
        /*
        binding = FragmentStudyplanLargeBinding.inflate(inflater, container, false)
        return binding
            .also { setupStudyPlan(it.studyplan) }
            .root

         */
    }

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    private fun setupComposeView(composeViewMyStudyPlan: ComposeView) {
        composeViewMyStudyPlan.setContent {
            MdcTheme {
                StudyPlanner(
                    viewModel = viewModel,
                    navigateUp = { findNavController().navigateUp() },
                    onProfile = ::openProfile
                )
            }
        }
    }

    private fun openProfile() {
        findNavController().navigate(R.id.action_study_planner_to_profile)
    }

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    private fun setupStudyPlan(studyplan: ComposeView) {
        studyplan.setContent {
            MdcTheme {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //postponeEnterTransition()
        TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
            .run {
                sharedElementEnterTransition = this
                sharedElementReturnTransition = this
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.textView.text = arguments?.getString("text_big")
        //val resource = arguments?.getInt("image_big")
        //if (resource != null) binding.imageView.setImageResource(resource)
        //startPostponedEnterTransition()
        /*
        viewModel.shippingProvider.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

         */
    }

    private fun setupShippingList(shippingList: RecyclerView) {
        shippingList.adapter = adapter
        shippingList.setHasFixedSize(true)
        shippingList.scheduleLayoutAnimation()
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.line)
            if (drawable != null) setDrawable(drawable)
        }
        shippingList.addItemDecoration(divider)
    }
}


@HiltViewModel
class StudyPlannerListViewModel @Inject constructor(
) : ViewModel() {
    val exampleList = listOf(
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

    val shippingProvider = MutableLiveData(exampleList)
}


class StudyPlannerListAdapter(
    private val onShippingProviderClicked: (view: SemesterBinding) -> Unit
) : ListAdapter<StudyPlan, StudyPlannerViewHolder>(IdEqualsDiffCallback { it.id }) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyPlannerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SemesterBinding.inflate(layoutInflater, parent, false)
            .run { StudyPlannerViewHolder(this, onShippingProviderClicked) }
    }

    override fun onBindViewHolder(holder: StudyPlannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

data class StudyPlan(val info: String, val id: Int)

data class Semester(val info: String, val id: Int)

class StudyPlannerViewHolder(
    val binding: SemesterBinding,
    val onShippingProviderClicked: (view: SemesterBinding) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(provider: StudyPlan) {
        binding.root.setOnClickListener {
            onShippingProviderClicked(binding)
        }
    }
}
