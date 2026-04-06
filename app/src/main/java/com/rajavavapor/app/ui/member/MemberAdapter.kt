package com.rajavavapor.app.ui.member

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.MemberItem
import java.text.NumberFormat
import java.util.Locale

class MemberAdapter(
    private val onClick: (MemberItem) -> Unit
) : ListAdapter<MemberItem, MemberAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View, private val onClick: (MemberItem) -> Unit) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvHp: TextView = view.findViewById(R.id.tvHp)
        val tvTier: TextView = view.findViewById(R.id.tvTier)
        val tvPoin: TextView = view.findViewById(R.id.tvPoin)
        val tvBelanja: TextView = view.findViewById(R.id.tvBelanja)
        val tvTransaksi: TextView = view.findViewById(R.id.tvTransaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvNama.text = item.nama
        holder.tvHp.text = item.noHp ?: "-"
        holder.tvTier.text = item.tier?.replaceFirstChar { it.uppercase() } ?: "Bronze"
        holder.tvTier.setBackgroundResource(tierBackground(item.tier))
        holder.tvPoin.text = "${item.totalPoin ?: 0} poin"
        holder.tvBelanja.text = (item.totalBelanja?.toDouble() ?: 0.0).toRupiah()
        holder.tvTransaksi.text = "${item.totalTransaksi ?: 0}x transaksi"
        holder.itemView.setOnClickListener { onClick(item) }
    }

    private fun Double.toRupiah(): String {
        val formatter = NumberFormat.getInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        return "Rp ${formatter.format(this)}"
    }

    private fun tierBackground(tier: String?) = when (tier?.lowercase()) {
        "platinum" -> R.drawable.tier_platinum
        "gold" -> R.drawable.tier_gold
        "silver" -> R.drawable.tier_silver
        else -> R.drawable.tier_bronze
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<MemberItem>() {
            override fun areItemsTheSame(a: MemberItem, b: MemberItem) = a.id == b.id
            override fun areContentsTheSame(a: MemberItem, b: MemberItem) = a == b
        }
    }
}
