package com.rajavavapor.app.ui.modules.monitoring

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.MonitoringOmzetItem
import java.text.NumberFormat
import java.util.Locale

class MonitoringAdapter : ListAdapter<MonitoringOmzetItem, MonitoringAdapter.ViewHolder>(DIFF) {

    private val rpFormat = NumberFormat.getInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCabang: TextView = view.findViewById(R.id.tvCabang)
        val tvOmzetHariIni: TextView = view.findViewById(R.id.tvOmzetHariIni)
        val tvTrxHariIni: TextView = view.findViewById(R.id.tvTrxHariIni)
        val tvOmzetBulan: TextView = view.findViewById(R.id.tvOmzetBulan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_monitoring, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvCabang.text = item.cabang
        holder.tvOmzetHariIni.text = "Rp ${rpFormat.format(item.omzetHariIni)}"
        holder.tvTrxHariIni.text = "${item.trxHariIni}"
        holder.tvOmzetBulan.text = "Rp ${rpFormat.format(item.omzetBulan)}"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<MonitoringOmzetItem>() {
            override fun areItemsTheSame(a: MonitoringOmzetItem, b: MonitoringOmzetItem) = a.cabang == b.cabang
            override fun areContentsTheSame(a: MonitoringOmzetItem, b: MonitoringOmzetItem) = a == b
        }
    }
}
