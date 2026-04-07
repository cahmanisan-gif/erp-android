package com.rajavavapor.app.ui.modules.omzet

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
import com.rajavavapor.app.databinding.FragmentOmzetCabangBinding
import com.rajavavapor.app.util.AnimationHelper
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OmzetCabangFragment : Fragment() {

    private var _binding: FragmentOmzetCabangBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OmzetCabangViewModel by viewModels()
    private lateinit var adapter: OmzetCabangAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOmzetCabangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Period label
        val bulan = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())
        binding.tvPeriode.text = "Periode: $bulan"

        adapter = OmzetCabangAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setColorSchemeResources(R.color.brand_red)
        binding.swipeRefresh.setOnRefreshListener { viewModel.load(requireContext()) }

        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmpty.isVisible = items.isEmpty()

            // Update header totals
            val fmt = NumberFormat.getInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
            val totalOmzet = items.sumOf { it.omzet ?: 0.0 }
            val totalTrx = items.sumOf { it.totalTrx ?: 0 }

            AnimationHelper.animateRupiah(binding.tvTotalOmzet, totalOmzet)
            binding.tvTotalTrx.text = "$totalTrx transaksi dari ${items.size} cabang"
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }

        viewModel.load(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
