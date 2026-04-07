package com.rajavavapor.app.ui.modules.omzet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.OmzetCabangItem
import com.rajavavapor.app.databinding.ItemOmzetCabangBinding
import java.text.NumberFormat
import java.util.Locale

class OmzetCabangAdapter :
    ListAdapter<OmzetCabangItem, OmzetCabangAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: ItemOmzetCabangBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOmzetCabangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val fmt = NumberFormat.getInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

        holder.binding.tvRank.text = "${position + 1}"
        holder.binding.tvNamaCabang.text = item.namaCabang ?: "Cabang"
        holder.binding.tvTrxCabang.text = "${item.totalTrx ?: 0} transaksi"
        holder.binding.tvOmzetCabang.text = "Rp ${fmt.format(item.omzet ?: 0.0)}"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<OmzetCabangItem>() {
            override fun areItemsTheSame(old: OmzetCabangItem, new: OmzetCabangItem) =
                old.cabangId == new.cabangId
            override fun areContentsTheSame(old: OmzetCabangItem, new: OmzetCabangItem) =
                old == new
        }
    }
}
