package com.rajavavapor.app.ui.modules.pos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.ProdukItem
import java.text.NumberFormat
import java.util.Locale

class PosAdapter(
    private val onClick: (ProdukItem) -> Unit
) : ListAdapter<ProdukItem, PosAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaProduk: TextView = view.findViewById(R.id.tvNamaProduk)
        val tvKodeProduk: TextView = view.findViewById(R.id.tvKodeProduk)
        val tvHarga: TextView = view.findViewById(R.id.tvHarga)
        val tvStok: TextView = view.findViewById(R.id.tvStok)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pos_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvNamaProduk.text = item.namaProduk
        holder.tvKodeProduk.text = item.kodeProduk ?: "-"
        holder.tvHarga.text = item.hargaJual.toDouble().toRupiah()
        holder.tvStok.text = "Stok: ${item.stok ?: 0}"
        holder.itemView.setOnClickListener { onClick(item) }
    }

    private fun Double.toRupiah(): String {
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(this)}"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ProdukItem>() {
            override fun areItemsTheSame(a: ProdukItem, b: ProdukItem) = a.id == b.id
            override fun areContentsTheSame(a: ProdukItem, b: ProdukItem) = a == b
        }
    }
}
