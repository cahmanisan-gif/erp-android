package com.rajavavapor.app.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.FaceEmployee
import com.rajavavapor.app.databinding.ItemEmployeeBinding

class EmployeeAdapter(
    private val onClick: (FaceEmployee) -> Unit
) : ListAdapter<FaceEmployee, EmployeeAdapter.ViewHolder>(DIFF) {

    class ViewHolder(
        private val binding: ItemEmployeeBinding,
        private val onClick: (FaceEmployee) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaceEmployee) {
            binding.tvEmployeeName.text = item.namaLengkap
            binding.tvEmployeeRole.text = item.role?.replaceFirstChar { it.uppercase() } ?: ""
            binding.tvAvatar.text = item.namaLengkap
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
        private val DIFF = object : DiffUtil.ItemCallback<FaceEmployee>() {
            override fun areItemsTheSame(old: FaceEmployee, new: FaceEmployee) = old.id == new.id
            override fun areContentsTheSame(old: FaceEmployee, new: FaceEmployee) = old == new
        }
    }
}
