package com.example.trailz.ui.studyplanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.*
import androidx.transition.TransitionInflater
import com.example.trailz.R
import com.example.trailz.databinding.FragmentStudyplanLargeBinding
import com.example.trailz.databinding.SemesterBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@AndroidEntryPoint
class StudyPlannerFragment : Fragment() {

    private val viewModel: StudyPlannerListViewModel by viewModels()
    lateinit var binding: FragmentStudyplanLargeBinding
    private val adapter: StudyPlannerListAdapter = StudyPlannerListAdapter {}
    private fun setupShippingList(shippingList: RecyclerView) {
        shippingList.adapter = adapter
    }

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyplanLargeBinding.inflate(inflater, container, false)
        return binding.apply {
        }.also {
            setupShippingList(it.recyclerView)
            val divider = DividerItemDecoration(
                it.recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
            divider.setDrawable(
                ContextCompat.getDrawable(
                    it.recyclerView.context,
                    R.drawable.line
                )!!
            )
            val mLayoutManager = LinearLayoutManager(activity)
            it.recyclerView.layoutManager = mLayoutManager
            it.recyclerView.scheduleLayoutAnimation()
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
        postponeEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text = arguments?.getString("text_big")
        binding.textView.text = text
        startPostponedEnterTransition()
        viewModel.shippingProvider.observe(viewLifecycleOwner) {
            adapter.submitList(it) {
                (view.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }
            }
        }
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
) : ListAdapter<StudyPlan, StudyPlannerViewHolder>(IdEqualsDiffCallback { it.info }) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyPlannerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SemesterBinding
            .inflate(layoutInflater, parent, false)
            .run { StudyPlannerViewHolder(this, onShippingProviderClicked) }
    }

    override fun onBindViewHolder(holder: StudyPlannerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

data class StudyPlan(val info: String, val id: Int)

data class Semester(val info: String, val id: Int)

class IdEqualsDiffCallback<T : Any, R>(
    private val idFunction: (T) -> R
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem::class == newItem::class &&
                idFunction(oldItem) == idFunction(newItem)
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

class StudyPlannerViewHolder(
    val binding: SemesterBinding,

    val onShippingProviderClicked: (view: SemesterBinding) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(provider: StudyPlan) {
        binding.textView.text = "${provider.id}"
        ViewCompat.setTransitionName(binding.textView, "text_small${provider.id}")
        //ViewCompat.setTransitionName(binding.imageView, "image_small${provider.id}")
        binding.root.setOnClickListener {
            onShippingProviderClicked(binding)
        }
    }
}
