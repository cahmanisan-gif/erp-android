package com.rajavavapor.app.ui.modules.piutang

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.PiutangItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PiutangAdapter : ListAdapter<PiutangItem, PiutangAdapter.ViewHolder>(DIFF) {

    private val rpFormat = NumberFormat.getInstance(Locale("id", "ID"))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCustomer: TextView = view.findViewById(R.id.tvCustomer)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val tvSisa: TextView = view.findViewById(R.id.tvSisa)
        val tvJatuhTempo: TextView = view.findViewById(R.id.tvJatuhTempo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_piutang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvCustomer.text = item.namaCustomer ?: "-"
        holder.tvTotal.text = "Total: Rp ${rpFormat.format(item.total)}"

        // Sisa - red if overdue
        val isOverdue = isOverdue(item.jatuhTempo)
        holder.tvSisa.text = "Sisa: Rp ${rpFormat.format(item.sisa)}"
        holder.tvSisa.setTextColor(if (isOverdue) Color.parseColor("#C1121F") else Color.parseColor("#212529"))

        // Jatuh tempo
        holder.tvJatuhTempo.text = if (item.jatuhTempo != null) "Jatuh tempo: ${item.jatuhTempo}" else ""
        if (isOverdue) holder.tvJatuhTempo.setTextColor(Color.parseColor("#C1121F"))
        else holder.tvJatuhTempo.setTextColor(Color.parseColor("#888888"))

        // Status badge
        val status = item.status ?: "-"
        holder.tvStatus.text = status.replaceFirstChar { it.uppercase() }
        when (status.lowercase()) {
            "lunas" -> holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"))
            "belum_lunas", "belum lunas" -> holder.tvStatus.setTextColor(Color.parseColor("#C1121F"))
            else -> holder.tvStatus.setTextColor(Color.parseColor("#666666"))
        }
    }

    private fun isOverdue(jatuhTempo: String?): Boolean {
        if (jatuhTempo == null) return false
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
            val dueDate = sdf.parse(jatuhTempo)
            dueDate != null && dueDate.before(Date())
        } catch (_: Exception) { false }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PiutangItem>() {
            override fun areItemsTheSame(a: PiutangItem, b: PiutangItem) = a.id == b.id
            override fun areContentsTheSame(a: PiutangItem, b: PiutangItem) = a == b
        }
    }
}
