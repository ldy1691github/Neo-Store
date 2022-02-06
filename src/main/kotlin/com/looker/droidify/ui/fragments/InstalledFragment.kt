package com.looker.droidify.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.looker.droidify.database.entity.Product
import com.looker.droidify.database.entity.Repository
import com.looker.droidify.databinding.FragmentInstalledXBinding
import com.looker.droidify.ui.items.HAppItem
import com.looker.droidify.ui.items.VAppItem
import com.looker.droidify.utility.PRODUCT_ASYNC_DIFFER_CONFIG
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.paged.PagedModelAdapter

class InstalledFragment : MainNavFragmentX() {

    private lateinit var binding: FragmentInstalledXBinding

    private lateinit var installedItemAdapter: PagedModelAdapter<Product, VAppItem>
    private var installedFastAdapter: FastAdapter<VAppItem>? = null
    private lateinit var updatedItemAdapter: PagedModelAdapter<Product, HAppItem>
    private var updatedFastAdapter: FastAdapter<HAppItem>? = null

    override val primarySource = Source.INSTALLED
    override val secondarySource = Source.UPDATES

    private var repositories: Map<Long, Repository> = mapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentInstalledXBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setupAdapters() {
        installedItemAdapter = PagedModelAdapter<Product, VAppItem>(PRODUCT_ASYNC_DIFFER_CONFIG) {
            it.item?.let { item -> VAppItem(item, repositories[it.repository_id]) }
        }
        updatedItemAdapter = PagedModelAdapter<Product, HAppItem>(PRODUCT_ASYNC_DIFFER_CONFIG) {
            // TODO filter for only updated apps and add placeholder
            it.item?.let { item -> HAppItem(item, repositories[it.repository_id]) }
        }
        installedFastAdapter = FastAdapter.with(installedItemAdapter)
        installedFastAdapter?.setHasStableIds(true)
        binding.installedRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = installedFastAdapter
        }
        updatedFastAdapter = FastAdapter.with(updatedItemAdapter)
        updatedFastAdapter?.setHasStableIds(true)
        binding.updatedRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = updatedFastAdapter
        }
    }

    override fun setupLayout() {
        binding.buttonUpdated.setOnClickListener {
            binding.updatedRecycler.apply {
                visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

        viewModel.primaryProducts.observe(viewLifecycleOwner) {
            installedItemAdapter.submitList(it)
        }
        viewModel.secondaryProducts.observe(viewLifecycleOwner) {
            binding.updatedBar.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            updatedItemAdapter.submitList(it)
        }
        viewModel.repositories.observe(viewLifecycleOwner) {
            repositories = it.associateBy { repo -> repo.id }
        }
    }
}
