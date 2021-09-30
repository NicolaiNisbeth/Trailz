package com.example.trailz.ui.studyplanners

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trailz.R
import com.example.trailz.databinding.FragmentStudyPlannersBinding
import com.example.trailz.inject.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudyPlannersFragment : Fragment() {

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentStudyPlannersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudyPlannersViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentStudyPlannersBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .also { setupClickListeners(it.profileBtn, it.studyPlannerBtn) }
            .run { root }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isError.observe(viewLifecycleOwner){ errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(viewLifecycleOwner){
            binding.progressbarView.visibility = if (it){
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.studyPlans.observe(viewLifecycleOwner){
            binding.studyPlansView.text = it.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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