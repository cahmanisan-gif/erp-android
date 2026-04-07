package com.rajavavapor.app.ui.modules.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.R
import com.rajavavapor.app.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaderboardViewModel by viewModels()
    private lateinit var adapter: LeaderboardAdapter

    // "kasir" or "cabang" — passed via arguments
    private var leaderboardType = "kasir"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leaderboardType = arguments?.getString("type") ?: "kasir"

        // Set title based on type
        if (leaderboardType == "kasir") {
            binding.tvLeaderboardTitle.text = "KASIR TERBAIK"
            binding.tvLeaderboardSubtitle.text = "TOP RANKING KASIR"
        } else {
            binding.tvLeaderboardTitle.text = "CABANG TERBAIK"
            binding.tvLeaderboardSubtitle.text = "TOP RANKING CABANG"
        }

        adapter = LeaderboardAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
        }

        // Period chip listeners
        setupPeriodChips()

        // Initial load
        viewModel.load(requireContext(), leaderboardType, "bulan-ini")
    }

    private fun setupPeriodChips() {
        val chipMap = mapOf(
            R.id.chipBulanIni to "bulan-ini",
            R.id.chip3Bulan to "3-bulan",
            R.id.chip6Bulan to "6-bulan",
            R.id.chip9Bulan to "9-bulan",
            R.id.chipTahunIni to "tahun-ini"
        )

        chipMap.forEach { (chipId, periode) ->
            binding.root.findViewById<Chip>(chipId)?.setOnClickListener {
                viewModel.load(requireContext(), leaderboardType, periode)
                // Replay animation
                binding.recyclerView.scheduleLayoutAnimation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
