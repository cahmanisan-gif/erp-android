package com.rajavavapor.app.ui.modules.topproduk

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
import com.rajavavapor.app.databinding.FragmentTopProdukDetailBinding
import com.rajavavapor.app.util.AnimationHelper
import java.text.NumberFormat
import java.util.Locale

class TopProdukDetailFragment : Fragment() {

    private var _binding: FragmentTopProdukDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TopProdukDetailViewModel by viewModels()
    private lateinit var adapter: ProdukCabangAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTopProdukDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get product info from arguments
        val namaProduk = arguments?.getString("nama_produk") ?: "Produk"
        val totalQty = arguments?.getInt("total_qty", 0) ?: 0
        val totalOmzet = arguments?.getDouble("total_omzet", 0.0) ?: 0.0

        binding.tvNamaProduk.text = namaProduk
        AnimationHelper.animateCounter(binding.tvTotalQty, totalQty)
        binding.tvTotalQty.text = "$totalQty pcs"

        val fmt = NumberFormat.getInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
        AnimationHelper.animateRupiah(binding.tvTotalOmzet, totalOmzet)

        adapter = ProdukCabangAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setColorSchemeResources(R.color.brand_red)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadByName(requireContext(), namaProduk)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { binding.swipeRefresh.isRefreshing = it }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmpty.isVisible = items.isEmpty()
            binding.tvJumlahCabang.text = "${items.size}"
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }

        viewModel.loadByName(requireContext(), namaProduk)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
