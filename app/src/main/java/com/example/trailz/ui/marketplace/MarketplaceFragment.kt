package com.example.trailz.ui.marketplace

/**
import Market_place
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.example.trailz.databinding.TestBinding
import com.example.trailz.databinding.TestElementBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
) : ViewModel() {
    val exampleList = listOf(ShippingProvider("1",0),
                             ShippingProvider("2",1),
                             ShippingProvider("3",2),
                             ShippingProvider("4",3))
    val shippingProvider = MutableLiveData(exampleList)
}

@AndroidEntryPoint
class MarketplaceFragment: Fragment() {

    private val viewModel: FavoritesViewModel by viewModels()
    private val adapter: RebooztShippingAdapter = RebooztShippingAdapter(
        onShippingProviderClicked = :: goToShippingProvider
    )
    private fun setupShippingList(shippingList: RecyclerView) {
        shippingList.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        return TestBinding.inflate(inflater, container, false).apply {
        }.also { setupShippingList(it.recyclerView) }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.shippingProvider.observe(viewLifecycleOwner, adapter::submitList)
    }

    fun goToShippingProvider(id: String){
        val direction = MarketplaceFragmentDirections.actionMarketplaceToStudyPlan(id)
        findNavController().navigate(directions = direction)
    }
}

class RebooztShippingAdapter(
    private val onShippingProviderClicked: (id:Int) -> Unit
) : ListAdapter<ShippingProvider, ShippingProviderViewHolder>(IdEqualsDiffCallback { it.info }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingProviderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TestElementBinding
            .inflate(layoutInflater, parent, false)
            .run { ShippingProviderViewHolder(this, onShippingProviderClicked) }
    }

    override fun onBindViewHolder(holder: ShippingProviderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}
data class ShippingProvider(val info: String, val id: Int){

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

class ShippingProviderViewHolder(
    val binding: TestElementBinding,
    val onShippingProviderClicked: (id:Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(provider: ShippingProvider) {
        binding.textView.text = provider.info
        binding.root.setOnClickListener { onShippingProviderClicked(provider.id) }
    }
}
 */