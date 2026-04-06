package com.rajavavapor.app.ui.modules.payroll

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
import com.rajavavapor.app.data.PayrollItem
import java.text.NumberFormat
import java.util.Locale

class PayrollAdapter : ListAdapter<PayrollItem, PayrollAdapter.ViewHolder>(DIFF) {

    private val rpFormat = NumberFormat.getInstance(Locale("id", "ID"))

    private val bulanNama = arrayOf(
        "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvPeriode: TextView = view.findViewById(R.id.tvPeriode)
        val tvGajiPokok: TextView = view.findViewById(R.id.tvGajiPokok)
        val tvTotalGaji: TextView = view.findViewById(R.id.tvTotalGaji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payroll, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvNama.text = item.namaKaryawan ?: "-"

        // Period display
        val namaBulan = if (item.bulan in 1..12) bulanNama[item.bulan] else "${item.bulan}"
        holder.tvPeriode.text = "$namaBulan ${item.tahun}"

        // Gaji pokok
        holder.tvGajiPokok.text = if (item.gajiPokok != null) "Pokok: Rp ${rpFormat.format(item.gajiPokok)}" else ""

        // Total gaji
        holder.tvTotalGaji.text = "Rp ${rpFormat.format(item.totalGaji)}"

        // Status badge
        val status = item.status ?: "pending"
        holder.tvStatus.text = status.replaceFirstChar { it.uppercase() }
        applyStatusBadge(holder.tvStatus, status)
    }

    private fun applyStatusBadge(tv: TextView, status: String) {
        val (textColor, bgColor) = when (status.lowercase()) {
            "dibayar", "paid", "selesai" -> Pair(Color.parseColor("#2E7D32"), Color.parseColor("#E8F5E9"))
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
        private val DIFF = object : DiffUtil.ItemCallback<PayrollItem>() {
            override fun areItemsTheSame(a: PayrollItem, b: PayrollItem) = a.id == b.id
            override fun areContentsTheSame(a: PayrollItem, b: PayrollItem) = a == b
        }
    }
}
