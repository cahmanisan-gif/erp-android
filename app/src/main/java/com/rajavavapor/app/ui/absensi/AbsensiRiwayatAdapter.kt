package com.rajavavapor.app.ui.absensi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.AbsensiRiwayatItem
import com.rajavavapor.app.databinding.ItemRiwayatAbsensiBinding

class AbsensiRiwayatAdapter :
    ListAdapter<AbsensiRiwayatItem, AbsensiRiwayatAdapter.ViewHolder>(DIFF) {

    class ViewHolder(private val binding: ItemRiwayatAbsensiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AbsensiRiwayatItem) {
            binding.tvTanggal.text = item.tanggal
            binding.tvClockIn.text = item.masuk ?: "-"
            binding.tvClockOut.text = item.pulang ?: "-"

            // Determine status from masuk/pulang
            val hasMasuk = !item.masuk.isNullOrEmpty()
            val hasPulang = !item.pulang.isNullOrEmpty()
            binding.tvStatus.isVisible = hasMasuk || hasPulang
            binding.tvStatus.text = when {
                hasMasuk && hasPulang -> "Lengkap"
                hasMasuk -> "Belum pulang"
                else -> ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatAbsensiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AbsensiRiwayatItem>() {
            override fun areItemsTheSame(old: AbsensiRiwayatItem, new: AbsensiRiwayatItem) =
                old.tanggal == new.tanggal

            override fun areContentsTheSame(old: AbsensiRiwayatItem, new: AbsensiRiwayatItem) =
                old == new
        }
    }
}
