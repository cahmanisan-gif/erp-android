package com.rajavavapor.app.ui.modules.kas

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.KasItem
import java.text.NumberFormat
import java.util.Locale

class KasAdapter : ListAdapter<KasItem, KasAdapter.ViewHolder>(DIFF) {

    private val rpFormat = NumberFormat.getInstance(Locale("id", "ID"))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvJenis: TextView = view.findViewById(R.id.tvJenis)
        val tvKeterangan: TextView = view.findViewById(R.id.tvKeterangan)
        val tvJumlah: TextView = view.findViewById(R.id.tvJumlah)
        val tvSaldo: TextView = view.findViewById(R.id.tvSaldo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kas, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvTanggal.text = item.tanggal ?: "-"
        holder.tvKeterangan.text = item.keterangan ?: "-"

        val isMasuk = item.jenis?.lowercase() == "masuk"
        holder.tvJenis.text = if (isMasuk) "Masuk" else "Keluar"
        holder.tvJenis.setTextColor(if (isMasuk) Color.parseColor("#2E7D32") else Color.parseColor("#C1121F"))

        val prefix = if (isMasuk) "+ " else "- "
        holder.tvJumlah.text = "${prefix}Rp ${rpFormat.format(item.jumlah)}"
        holder.tvJumlah.setTextColor(if (isMasuk) Color.parseColor("#2E7D32") else Color.parseColor("#C1121F"))

        holder.tvSaldo.text = if (item.saldo != null) "Saldo: Rp ${rpFormat.format(item.saldo)}" else ""
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<KasItem>() {
            override fun areItemsTheSame(a: KasItem, b: KasItem) = a.id == b.id
            override fun areContentsTheSame(a: KasItem, b: KasItem) = a == b
        }
    }
}
