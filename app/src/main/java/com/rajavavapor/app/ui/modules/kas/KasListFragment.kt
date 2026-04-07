package com.rajavavapor.app.ui.modules.kas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.R
import com.rajavavapor.app.data.KasItem
import com.rajavavapor.app.databinding.FragmentKasBinding
import com.rajavavapor.app.util.AnimationHelper
import java.text.NumberFormat
import java.util.Locale

class KasListFragment : Fragment() {

    private var _binding: FragmentKasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: KasListViewModel by viewModels()
    private lateinit var adapter: KasAdapter
    private var currentFilter = "semua"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = KasAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setColorSchemeResources(R.color.brand_red)
        binding.swipeRefresh.setOnRefreshListener { viewModel.load(requireContext()) }

        // Filter tabs
        binding.chipSemua.setOnClickListener { currentFilter = "semua"; applyFilter() }
        binding.chipMasuk.setOnClickListener { currentFilter = "masuk"; applyFilter() }
        binding.chipKeluar.setOnClickListener { currentFilter = "keluar"; applyFilter() }

        // Search
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.filter(s?.toString()?.trim() ?: "")
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            applyFilter()
            updateSummary(items)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }

        viewModel.load(requireContext())
    }

    private fun applyFilter() {
        val allItems = viewModel.items.value ?: emptyList()
        val filtered = when (currentFilter) {
            "masuk" -> allItems.filter { it.jenis?.lowercase() == "masuk" }
            "keluar" -> allItems.filter { it.jenis?.lowercase() == "keluar" }
            else -> allItems
        }
        adapter.submitList(filtered)
        binding.tvEmpty.isVisible = filtered.isEmpty()
        binding.recyclerView.scheduleLayoutAnimation()
    }

    private fun updateSummary(items: List<KasItem>) {
        val fmt = NumberFormat.getInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
        val totalMasuk = items.filter { it.jenis?.lowercase() == "masuk" }.sumOf { it.jumlah ?: 0L }
        val totalKeluar = items.filter { it.jenis?.lowercase() == "keluar" }.sumOf { it.jumlah ?: 0L }
        val saldo = totalMasuk - totalKeluar

        AnimationHelper.animateRupiah(binding.tvSaldo, saldo.toDouble())
        binding.tvTotalMasuk.text = "Rp ${fmt.format(totalMasuk)}"
        binding.tvTotalKeluar.text = "Rp ${fmt.format(totalKeluar)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
