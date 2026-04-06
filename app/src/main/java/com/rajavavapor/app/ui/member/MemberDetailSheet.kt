package com.rajavavapor.app.ui.member

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajavavapor.app.R
import com.rajavavapor.app.data.MemberItem
import com.rajavavapor.app.databinding.SheetMemberDetailBinding
import java.text.NumberFormat
import java.util.Locale

class MemberDetailSheet : BottomSheetDialogFragment() {

    private var _binding: SheetMemberDetailBinding? = null
    private val binding get() = _binding!!

    private var item: MemberItem? = null

    fun setItem(member: MemberItem): MemberDetailSheet {
        this.item = member
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SheetMemberDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val m = item ?: return dismiss()

        binding.tvSheetAvatar.text = m.nama.take(1).uppercase()
        binding.tvSheetNama.text = m.nama
        binding.tvSheetHp.text = m.noHp ?: "-"

        // Tier badge
        val tier = m.tier?.lowercase() ?: "bronze"
        binding.tvSheetTier.text = tier.replaceFirstChar { it.uppercase() }
        val tierBg = when (tier) {
            "platinum" -> R.drawable.tier_platinum
            "gold" -> R.drawable.tier_gold
            "silver" -> R.drawable.tier_silver
            else -> R.drawable.tier_bronze
        }
        binding.tvSheetTier.setBackgroundResource(tierBg)
        binding.tvSheetTier.setTextColor(ContextCompat.getColor(requireContext(),
            if (tier == "gold" || tier == "silver") R.color.dark_bg else android.R.color.white))

        // Stats
        binding.tvSheetPoin.text = (m.totalPoin ?: 0).toString()
        binding.tvSheetBelanja.text = (m.totalBelanja ?: 0).toRupiah()
        binding.tvSheetTransaksi.text = (m.totalTransaksi ?: 0).toString()
    }

    private fun Long.toRupiah(): String {
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(this)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
