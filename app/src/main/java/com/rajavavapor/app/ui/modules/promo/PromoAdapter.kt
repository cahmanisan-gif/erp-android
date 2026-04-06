package com.rajavavapor.app.ui.modules.promo

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
import com.rajavavapor.app.data.PromoItem

class PromoAdapter : ListAdapter<PromoItem, PromoAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDeskripsi: TextView = view.findViewById(R.id.tvDeskripsi)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvNama.text = item.nama
        holder.tvDeskripsi.text = item.deskripsi ?: "-"

        // Date range
        val mulai = item.tanggalMulai ?: "-"
        val selesai = item.tanggalSelesai ?: "-"
        holder.tvTanggal.text = "$mulai  s/d  $selesai"

        // Active status badge
        val isAktif = item.isAktif ?: false
        holder.tvStatus.text = if (isAktif) "Aktif" else "Nonaktif"
        val (textColor, bgColor) = if (isAktif) {
            Pair(Color.parseColor("#2E7D32"), Color.parseColor("#E8F5E9"))
        } else {
            Pair(Color.parseColor("#888888"), Color.parseColor("#F5F5F5"))
        }
        holder.tvStatus.setTextColor(textColor)
        val bg = GradientDrawable()
        bg.cornerRadius = 16f
        bg.setColor(bgColor)
        holder.tvStatus.background = bg
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PromoItem>() {
            override fun areItemsTheSame(a: PromoItem, b: PromoItem) = a.id == b.id
            override fun areContentsTheSame(a: PromoItem, b: PromoItem) = a == b
        }
    }
}
