package com.rajavavapor.app.ui.modules.izin

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.IzinItem

class IzinAdapter : ListAdapter<IzinItem, IzinAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTipe: TextView = view.findViewById(R.id.tvTipe)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvAlasan: TextView = view.findViewById(R.id.tvAlasan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_izin, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvTipe.text = item.tipe?.replaceFirstChar { it.uppercase() } ?: "-"
        holder.tvTanggal.text = item.tanggal ?: "-"
        holder.tvAlasan.text = item.alasan ?: "-"

        val status = item.status ?: "pending"
        holder.tvStatus.text = status.replaceFirstChar { it.uppercase() }
        applyStatusBadge(holder.tvStatus, status)
    }

    private fun applyStatusBadge(tv: TextView, status: String) {
        val (textColor, bgColor) = when (status.lowercase()) {
            "disetujui", "approved" -> Pair(Color.parseColor("#2E7D32"), Color.parseColor("#E8F5E9"))
            "ditolak", "rejected" -> Pair(Color.parseColor("#C62828"), Color.parseColor("#FFEBEE"))
            else -> Pair(Color.parseColor("#E65100"), Color.parseColor("#FFF3E0"))
        }
        tv.setTextColor(textColor)
        val bg = GradientDrawable()
        bg.cornerRadius = 16f
        bg.setColor(bgColor)
        tv.background = bg
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<IzinItem>() {
            override fun areItemsTheSame(a: IzinItem, b: IzinItem) = a.id == b.id
            override fun areContentsTheSame(a: IzinItem, b: IzinItem) = a == b
        }
    }
}
