package com.rajavavapor.app.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.FaceCabang
import com.rajavavapor.app.databinding.ItemEmployeeBinding

class CabangAdapter(
    private val onClick: (FaceCabang) -> Unit
) : ListAdapter<FaceCabang, CabangAdapter.ViewHolder>(DIFF) {

    class ViewHolder(
        private val binding: ItemEmployeeBinding,
        private val onClick: (FaceCabang) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaceCabang) {
            binding.tvEmployeeName.text = item.nama
            binding.tvEmployeeRole.text = item.kode ?: ""
            binding.tvAvatar.text = item.nama
                .split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercase() }
                .joinToString("")

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FaceCabang>() {
            override fun areItemsTheSame(old: FaceCabang, new: FaceCabang) = old.id == new.id
            override fun areContentsTheSame(old: FaceCabang, new: FaceCabang) = old == new
        }
    }
}
