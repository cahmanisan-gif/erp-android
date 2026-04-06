package com.rajavavapor.app.ui.modules.request

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
import com.rajavavapor.app.data.RequestProdukItem

class RequestAdapter : ListAdapter<RequestProdukItem, RequestAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaProduk: TextView = view.findViewById(R.id.tvNamaProduk)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvCabang: TextView = view.findViewById(R.id.tvCabang)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvNamaProduk.text = item.namaProduk ?: "-"
        holder.tvQty.text = "Qty: ${item.qty ?: 0}"
        holder.tvCabang.text = item.namaCabang ?: "-"
        holder.tvTanggal.text = item.tanggal ?: "-"

        val status = item.status ?: "pending"
        holder.tvStatus.text = status.replaceFirstChar { it.uppercase() }
        applyStatusBadge(holder.tvStatus, status)
    }

    private fun applyStatusBadge(tv: TextView, status: String) {
        val (textColor, bgColor) = when (status.lowercase()) {
            "disetujui", "approved", "selesai" -> Pair(Color.parseColor("#2E7D32"), Color.parseColor("#E8F5E9"))
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
        private val DIFF = object : DiffUtil.ItemCallback<RequestProdukItem>() {
            override fun areItemsTheSame(a: RequestProdukItem, b: RequestProdukItem) = a.id == b.id
            override fun areContentsTheSame(a: RequestProdukItem, b: RequestProdukItem) = a == b
        }
    }
}
