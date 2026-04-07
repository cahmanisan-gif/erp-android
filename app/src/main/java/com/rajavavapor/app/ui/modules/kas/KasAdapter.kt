package com.rajavavapor.app.ui.modules.kas

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.KasItem
import com.rajavavapor.app.databinding.ItemKasBinding
import java.text.NumberFormat
import java.util.Locale

class KasAdapter : ListAdapter<KasItem, KasAdapter.ViewHolder>(DIFF) {

    private val fmt = NumberFormat.getInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

    class ViewHolder(val binding: ItemKasBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val isMasuk = item.jenis?.lowercase() == "masuk"

        // Icon circle
        val iconColor = if (isMasuk) Color.parseColor("#2E7D32") else Color.parseColor("#C1121F")
        holder.binding.tvIcon.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(iconColor)
        }
        holder.binding.tvIcon.text = if (isMasuk) "+" else "-"

        // Description + date
        holder.binding.tvKeterangan.text = item.keterangan ?: "Transaksi"
        holder.binding.tvTanggal.text = item.tanggal ?: "-"

        // Amount — green for masuk, red for keluar
        val prefix = if (isMasuk) "+" else "-"
        holder.binding.tvJumlah.text = "${prefix}Rp ${fmt.format(item.jumlah ?: 0)}"
        holder.binding.tvJumlah.setTextColor(iconColor)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<KasItem>() {
            override fun areItemsTheSame(a: KasItem, b: KasItem) = a.id == b.id
            override fun areContentsTheSame(a: KasItem, b: KasItem) = a == b
        }
    }
}
