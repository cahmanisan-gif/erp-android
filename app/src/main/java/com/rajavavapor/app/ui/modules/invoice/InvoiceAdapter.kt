package com.rajavavapor.app.ui.modules.invoice

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
import com.rajavavapor.app.data.InvoiceItem
import java.text.NumberFormat
import java.util.Locale

class InvoiceAdapter(
    private val onClick: (InvoiceItem) -> Unit
) : ListAdapter<InvoiceItem, InvoiceAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoInvoice: TextView = view.findViewById(R.id.tvNoInvoice)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvCustomer: TextView = view.findViewById(R.id.tvCustomer)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_invoice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvNoInvoice.text = item.noInvoice
        holder.tvTanggal.text = item.tanggal
        holder.tvCustomer.text = item.namaCustomer ?: "-"
        holder.tvTotal.text = item.total.toDouble().toRupiah()

        val status = item.status ?: "pending"
        holder.tvStatus.text = status.replaceFirstChar { it.uppercase() }
        applyStatusBadge(holder.tvStatus, status)

        holder.itemView.setOnClickListener { onClick(item) }
    }

    private fun applyStatusBadge(tv: TextView, status: String) {
        val (textColor, bgColor) = when (status.lowercase()) {
            "lunas", "paid", "selesai" -> Pair(Color.parseColor("#2E7D32"), Color.parseColor("#E8F5E9"))
            "batal", "cancelled" -> Pair(Color.parseColor("#C62828"), Color.parseColor("#FFEBEE"))
            else -> Pair(Color.parseColor("#E65100"), Color.parseColor("#FFF3E0"))
        }
        tv.setTextColor(textColor)
        val bg = GradientDrawable()
        bg.cornerRadius = 16f
        bg.setColor(bgColor)
        tv.background = bg
    }

    private fun Double.toRupiah(): String {
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(this)}"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<InvoiceItem>() {
            override fun areItemsTheSame(a: InvoiceItem, b: InvoiceItem) = a.id == b.id
            override fun areContentsTheSame(a: InvoiceItem, b: InvoiceItem) = a == b
        }
    }
}
