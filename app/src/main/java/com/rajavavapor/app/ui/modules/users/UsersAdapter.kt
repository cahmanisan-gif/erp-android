package com.rajavavapor.app.ui.modules.users

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.R
import com.rajavavapor.app.data.UserListItem

class UsersAdapter : ListAdapter<UserListItem, UsersAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        val tvRole: TextView = view.findViewById(R.id.tvRole)
        val tvCabang: TextView = view.findViewById(R.id.tvCabang)
        val viewAvatar: View = view.findViewById(R.id.viewAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        // Initial letter
        val name = item.namaLengkap ?: item.username
        holder.tvInitial.text = name.firstOrNull()?.uppercase() ?: "?"

        holder.tvNama.text = item.namaLengkap ?: item.username
        holder.tvUsername.text = "@${item.username}"
        holder.tvRole.text = item.role?.replaceFirstChar { it.uppercase() } ?: "-"
        holder.tvCabang.text = item.namaCabang ?: ""

        // Active status badge
        val isAktif = item.isAktif ?: true
        holder.tvStatus.text = if (isAktif) "Aktif" else "Nonaktif"
        val (textColor, bgColor) = if (isAktif) {
            Pair(Color.parseColor("#2E7D32"), Color.parseColor("#E8F5E9"))
        } else {
            Pair(Color.parseColor("#888888"), Color.parseColor("#F5F5F5"))
        }
        holder.tvStatus.setTextColor(textColor)
        val bg = GradientDrawable()
        bg.cornerRadius = 16f
        bg.setColor(bgColor)
        holder.tvStatus.background = bg

        // Avatar color based on position
        val colors = arrayOf("#C1121F", "#1565C0", "#2E7D32", "#6A1B9A", "#E65100")
        val avatarBg = holder.viewAvatar.background as? GradientDrawable
        if (avatarBg != null) {
            avatarBg.setColor(Color.parseColor(colors[position % colors.size]))
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<UserListItem>() {
            override fun areItemsTheSame(a: UserListItem, b: UserListItem) = a.id == b.id
            override fun areContentsTheSame(a: UserListItem, b: UserListItem) = a == b
        }
    }
}
