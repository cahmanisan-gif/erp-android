package com.rajavavapor.app.ui.notifikasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajavavapor.app.data.NotifikasiItem
import com.rajavavapor.app.databinding.SheetNotifikasiDetailBinding

class NotifikasiDetailSheet : BottomSheetDialogFragment() {

    private var _binding: SheetNotifikasiDetailBinding? = null
    private val binding get() = _binding!!

    private var item: NotifikasiItem? = null

    fun setItem(notifikasi: NotifikasiItem): NotifikasiDetailSheet {
        this.item = notifikasi
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SheetNotifikasiDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val n = item ?: return dismiss()

        binding.tvSheetTipe.text = formatTipe(n.tipe)
        binding.tvSheetJudul.text = n.judul
        binding.tvSheetWaktu.text = n.createdAt
        binding.tvSheetPesan.text = n.pesan ?: "Tidak ada detail tambahan."

        if (!n.link.isNullOrEmpty()) {
            binding.btnSheetAction.isVisible = true
            binding.btnSheetAction.setOnClickListener {
                // Could open link in browser or navigate
                dismiss()
            }
        }
    }

    private fun formatTipe(tipe: String?): String = when (tipe) {
        "stok" -> "STOK"
        "retur" -> "RETUR"
        "request" -> "REQUEST"
        "kasbon" -> "KASBON"
        "piutang" -> "PIUTANG"
        "absensi" -> "ABSENSI"
        "payroll" -> "PAYROLL"
        "system" -> "SISTEM"
        else -> (tipe ?: "INFO").uppercase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
