package com.rajavavapor.app.ui.modules.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.CustomerItem
import java.text.NumberFormat
import java.util.Locale

class CustomerAdapter(
    private val onClick: (CustomerItem) -> Unit
) : ListAdapter<CustomerItem, CustomerAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvHp: TextView = view.findViewById(R.id.tvHp)
        val tvAlamat: TextView = view.findViewById(R.id.tvAlamat)
        val tvBelanja: TextView = view.findViewById(R.id.tvBelanja)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvNama.text = item.nama
        holder.tvHp.text = item.noHp ?: "-"
        holder.tvAlamat.text = item.alamat ?: "-"
        holder.tvBelanja.text = (item.totalBelanja?.toDouble() ?: 0.0).toRupiah()
        holder.itemView.setOnClickListener { onClick(item) }
    }

    private fun Double.toRupiah(): String {
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(this)}"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CustomerItem>() {
            override fun areItemsTheSame(a: CustomerItem, b: CustomerItem) = a.id == b.id
            override fun areContentsTheSame(a: CustomerItem, b: CustomerItem) = a == b
        }
    }
}
