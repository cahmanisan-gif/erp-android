package com.rajavavapor.app.ui.modules.pembelian

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
import com.rajavavapor.app.data.PembelianItem
import java.text.NumberFormat
import java.util.Locale

class PembelianAdapter : ListAdapter<PembelianItem, PembelianAdapter.ViewHolder>(DIFF) {

    private val rpFormat = NumberFormat.getInstance(Locale("id", "ID"))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoPo: TextView = view.findViewById(R.id.tvNoPo)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvSupplier: TextView = view.findViewById(R.id.tvSupplier)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pembelian, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvNoPo.text = item.noPo ?: "-"
        holder.tvTanggal.text = item.tanggal ?: "-"
        holder.tvSupplier.text = item.supplier ?: "-"
        holder.tvTotal.text = "Rp ${rpFormat.format(item.total)}"

        val status = item.status ?: "pending"
        holder.tvStatus.text = status.replaceFirstChar { it.uppercase() }
        applyStatusBadge(holder.tvStatus, status)
    }

    private fun applyStatusBadge(tv: TextView, status: String) {
        val (textColor, bgColor) = when (status.lowercase()) {
            "selesai", "diterima", "paid" -> Pair(Color.parseColor("#2E7D32"), Color.parseColor("#E8F5E9"))
            "batal", "cancelled" -> Pair(Color.parseColor("#C62828"), Color.parseColor("#FFEBEE"))
            else -> Pair(Color.parseColor("#E65100"), Color.parseColor("#FFF3E0"))
        }
        tv.setTextColor(textColor)
        val bg = GradientDrawable()
        bg.cornerRadius = 16f
        bg.setColor(bgColor)
        tv.background = bg
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PembelianItem>() {
            override fun areItemsTheSame(a: PembelianItem, b: PembelianItem) = a.id == b.id
            override fun areContentsTheSame(a: PembelianItem, b: PembelianItem) = a == b
        }
    }
}
