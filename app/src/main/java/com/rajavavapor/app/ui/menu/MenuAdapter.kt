package com.rajavavapor.app.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.databinding.ItemMenuBinding

class MenuAdapter(
    private val items: List<MenuItem>,
    private val onClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val ctx = holder.binding.root.context

        holder.binding.tvMenuTitle.text = item.title
        holder.binding.ivIcon.setImageResource(item.icon)
        holder.binding.iconBg.backgroundTintList =
            ContextCompat.getColorStateList(ctx, item.color)

        holder.binding.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
