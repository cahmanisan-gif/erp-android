package com.rajavavapor.app.ui.modules.laporan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.rajavavapor.app.databinding.FragmentLaporanBinding

class LaporanFragment : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaporanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardLabaRugi.setOnClickListener {
            Snackbar.make(binding.root, "Laporan Laba Rugi - segera hadir", Snackbar.LENGTH_SHORT).show()
        }
        binding.cardNeraca.setOnClickListener {
            Snackbar.make(binding.root, "Laporan Neraca - segera hadir", Snackbar.LENGTH_SHORT).show()
        }
        binding.cardArusKas.setOnClickListener {
            Snackbar.make(binding.root, "Laporan Arus Kas - segera hadir", Snackbar.LENGTH_SHORT).show()
        }
        binding.cardPenjualan.setOnClickListener {
            Snackbar.make(binding.root, "Laporan Penjualan - segera hadir", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
