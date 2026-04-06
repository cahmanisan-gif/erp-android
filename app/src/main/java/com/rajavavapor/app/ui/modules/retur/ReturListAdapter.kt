package com.rajavavapor.app.ui.modules.retur

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.ReturItem
import java.text.NumberFormat
import java.util.Locale

class ReturListAdapter : ListAdapter<ReturItem, ReturListAdapter.ViewHolder>(DIFF) {

    private val rupiahFormat = NumberFormat.getInstance(Locale("id", "ID"))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoRetur: TextView = view.findViewById(R.id.tvNoRetur)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvCabang: TextView = view.findViewById(R.id.tvCabang)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_retur, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.itemView.context

        holder.tvNoRetur.text = item.noRetur ?: "-"
        holder.tvTanggal.text = item.tanggal ?: "-"
        holder.tvCabang.text = item.namaCabang ?: "-"

        // Total formatted as Rupiah
        val total = item.total ?: 0L
        holder.tvTotal.text = "Rp ${rupiahFormat.format(total)}"

        // Status badge
        val status = item.status?.lowercase() ?: ""
        holder.tvStatus.text = item.status?.replaceFirstChar { it.uppercase() } ?: "-"
        when {
            status.contains("pending") || status.contains("menunggu") -> {
                holder.tvStatus.setBackgroundResource(R.drawable.badge_pending)
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.brand_red))
            }
            status.contains("approved") || status.contains("selesai") || status.contains("disetujui") -> {
                holder.tvStatus.setBackgroundResource(R.drawable.badge_approved)
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.success_green))
            }
            else -> {
                holder.tvStatus.setBackgroundResource(R.drawable.badge_background)
                holder.tvStatus.setTextColor(0xFF888888.toInt())
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ReturItem>() {
            override fun areItemsTheSame(a: ReturItem, b: ReturItem) = a.id == b.id
            override fun areContentsTheSame(a: ReturItem, b: ReturItem) = a == b
        }
    }
}
