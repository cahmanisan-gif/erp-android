package com.rajavavapor.app.ui.notifikasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.databinding.FragmentNotifikasiBinding
import com.rajavavapor.app.util.ScreenHelper

class NotifikasiFragment : Fragment() {

    private var _binding: FragmentNotifikasiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotifikasiViewModel by viewModels()
    private lateinit var adapter: NotifikasiAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotifikasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotifikasiAdapter { item ->
            if (item.dibaca == 0) {
                viewModel.bacaNotifikasi(requireContext(), item.id)
            }
            // Show detail bottom sheet
            NotifikasiDetailSheet().setItem(item)
                .show(childFragmentManager, "notifikasi_detail")
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), ScreenHelper.getGridColumns(requireContext()))
        binding.recyclerView.adapter = adapter

        binding.swipeRefresh.setColorSchemeResources(com.rajavavapor.app.R.color.brand_red)
        binding.swipeRefresh.setOnRefreshListener { viewModel.load(requireContext()) }

        binding.btnBacaSemua.setOnClickListener {
            viewModel.bacaSemua(requireContext())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.swipeRefresh.isRefreshing = loading
        }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            val unread = items.count { it.dibaca == 0 }
            binding.tvUnreadCount.text = if (unread > 0) "$unread belum dibaca" else "Semua sudah dibaca"
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
