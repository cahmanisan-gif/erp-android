package com.rajavavapor.app.ui.modules.produk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.ProdukListItem
import java.text.NumberFormat
import java.util.Locale

class ProdukListAdapter : ListAdapter<ProdukListItem, ProdukListAdapter.ViewHolder>(DIFF) {

    private val rupiahFormat = NumberFormat.getInstance(Locale("id", "ID"))

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvKategori: TextView = view.findViewById(R.id.tvKategori)
        val tvKode: TextView = view.findViewById(R.id.tvKode)
        val tvHarga: TextView = view.findViewById(R.id.tvHarga)
        val tvStok: TextView = view.findViewById(R.id.tvStok)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.itemView.context

        holder.tvNama.text = item.namaProduk

        // Kategori badge
        if (!item.kategori.isNullOrEmpty()) {
            holder.tvKategori.text = item.kategori
            holder.tvKategori.visibility = View.VISIBLE
        } else {
            holder.tvKategori.visibility = View.GONE
        }

        // Kode produk
        holder.tvKode.text = item.kodeProduk ?: "-"

        // Harga formatted as Rupiah
        val harga = item.hargaJual ?: 0L
        holder.tvHarga.text = "Rp ${rupiahFormat.format(harga)}"

        // Stok with color coding
        val stok = item.stok ?: 0
        holder.tvStok.text = "Stok: $stok"
        val stokColor = when {
            stok < 5 -> ContextCompat.getColor(ctx, R.color.brand_red)
            stok > 20 -> ContextCompat.getColor(ctx, R.color.success_green)
            else -> 0xFF888888.toInt()
        }
        holder.tvStok.setTextColor(stokColor)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ProdukListItem>() {
            override fun areItemsTheSame(a: ProdukListItem, b: ProdukListItem) = a.id == b.id
            override fun areContentsTheSame(a: ProdukListItem, b: ProdukListItem) = a == b
        }
    }
}
