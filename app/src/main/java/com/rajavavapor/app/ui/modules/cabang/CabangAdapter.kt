package com.rajavavapor.app.ui.modules.cabang

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
import com.rajavavapor.app.data.CabangListItem

class CabangAdapter : ListAdapter<CabangListItem, CabangAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvKode: TextView = view.findViewById(R.id.tvKode)
        val tvAlamat: TextView = view.findViewById(R.id.tvAlamat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cabang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvNama.text = item.nama
        holder.tvKode.text = item.kode ?: "-"
        holder.tvAlamat.text = item.alamat ?: "-"

        // Active status badge
        val isAktif = item.isAktif ?: true
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
        private val DIFF = object : DiffUtil.ItemCallback<CabangListItem>() {
            override fun areItemsTheSame(a: CabangListItem, b: CabangListItem) = a.id == b.id
            override fun areContentsTheSame(a: CabangListItem, b: CabangListItem) = a == b
        }
    }
}
