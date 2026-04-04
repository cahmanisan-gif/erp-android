package com.rajavavapor.app.ui.notifikasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.NotifikasiItem

class NotifikasiAdapter(
    private val onClick: (NotifikasiItem) -> Unit
) : ListAdapter<NotifikasiItem, NotifikasiAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dot: View = view.findViewById(R.id.viewUnreadDot)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvPesan: TextView = view.findViewById(R.id.tvPesan)
        val tvTipe: TextView = view.findViewById(R.id.tvTipe)
        val tvWaktu: TextView = view.findViewById(R.id.tvWaktu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notifikasi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvJudul.text = item.judul
        holder.tvPesan.text = item.pesan ?: ""
        holder.tvPesan.visibility = if (item.pesan.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.tvTipe.text = formatTipe(item.tipe)
        holder.tvWaktu.text = formatWaktu(item.createdAt)
        holder.dot.visibility = if (item.dibaca == 0) View.VISIBLE else View.INVISIBLE
        holder.itemView.alpha = if (item.dibaca == 1) 0.6f else 1.0f
        holder.itemView.setOnClickListener { onClick(item) }
    }

    private fun formatTipe(tipe: String?): String = when (tipe) {
        "stok" -> "Stok"
        "retur" -> "Retur"
        "request" -> "Request"
        "kasbon" -> "Kasbon"
        "piutang" -> "Piutang"
        "absensi" -> "Absensi"
        "payroll" -> "Payroll"
        "system" -> "Sistem"
        else -> tipe?.replaceFirstChar { it.uppercase() } ?: "Info"
    }

    private fun formatWaktu(dateStr: String): String {
        return try {
            // Format: 2026-04-04T12:00:00.000Z → "4 Apr 2026 12:00"
            val parts = dateStr.replace("T", " ").take(16)
            parts
        } catch (_: Exception) { dateStr }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NotifikasiItem>() {
            override fun areItemsTheSame(a: NotifikasiItem, b: NotifikasiItem) = a.id == b.id
            override fun areContentsTheSame(a: NotifikasiItem, b: NotifikasiItem) = a == b
        }
    }
}
