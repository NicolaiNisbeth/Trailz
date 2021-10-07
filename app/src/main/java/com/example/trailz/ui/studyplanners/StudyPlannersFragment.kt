package com.example.trailz.ui.studyplanners

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.material.*
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trailz.R
import com.example.trailz.databinding.TestBinding
import com.example.trailz.databinding.TestElementBinding
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class StudyPlannersFragment : Fragment() {

    private val viewModel: StudyPlanListViewModel by viewModels()
    private val adapter: StudyPlanListAdapter = StudyPlanListAdapter(
        onShippingProviderClicked = :: openStudyPlan
    )
    private fun setupShippingList(shippingList: RecyclerView) {
        shippingList.adapter = adapter
    }

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TestBinding.inflate(inflater, container, false).apply {
        }.also { setupShippingList(it.recyclerView) }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        viewModel.shippingProvider.observe(viewLifecycleOwner){
            adapter.submitList(it) {
                (view.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }
            }
        }
    }

    private fun openStudyPlan(id:View, post:String){
        val extra = FragmentNavigatorExtras(id to post)
        findNavController().navigate(
            R.id.action_study_planners_to_study_planner,
            null,
            null,
            extra)
    }
}


@HiltViewModel
class StudyPlanListViewModel @Inject constructor(
) : ViewModel() {
    val exampleList = listOf(StudyPlan("1",0),
        StudyPlan("2",1),
        StudyPlan("3",2),
        StudyPlan("4",3))
    val shippingProvider = MutableLiveData(exampleList)
}


class StudyPlanListAdapter(
    private val onShippingProviderClicked: (id:View, post:String) -> Unit
) : ListAdapter<StudyPlan, StudyPlanViewHolder>(IdEqualsDiffCallback { it.info }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyPlanViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TestElementBinding
            .inflate(layoutInflater, parent, false)
            .run { StudyPlanViewHolder(this, onShippingProviderClicked) }
    }

    override fun onBindViewHolder(holder: StudyPlanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}
data class StudyPlan(val info: String, val id: Int){

}

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

class StudyPlanViewHolder(
    val binding: TestElementBinding,

    val onShippingProviderClicked: (id:View, post:String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(provider: StudyPlan) {
        binding.root.setOnClickListener {
            onShippingProviderClicked(binding.textView, "image_big")
        }
    }
}