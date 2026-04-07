package com.rajavavapor.app.ui.modules.topproduk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.ProdukCabangItem
import com.rajavavapor.app.databinding.ItemProdukCabangBinding

class ProdukCabangAdapter :
    ListAdapter<ProdukCabangItem, ProdukCabangAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: ItemProdukCabangBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProdukCabangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.tvCabangNama.text = item.namaCabang ?: "Cabang"
        holder.binding.tvCabangKode.text = item.kodeCabang ?: ""
        holder.binding.tvQty.text = "${item.qty ?: 0}"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ProdukCabangItem>() {
            override fun areItemsTheSame(old: ProdukCabangItem, new: ProdukCabangItem) =
                old.cabangId == new.cabangId
            override fun areContentsTheSame(old: ProdukCabangItem, new: ProdukCabangItem) =
                old == new
        }
    }
}
