package com.rajavavapor.app.ui.modules.leaderboard

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.LeaderboardItem
import com.rajavavapor.app.databinding.ItemLeaderboardBinding

class LeaderboardAdapter(
    private val currentUserName: String? = null
) : ListAdapter<LeaderboardItem, LeaderboardAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val dp = holder.binding.root.context.resources.displayMetrics.density
        val name = item.getDisplayName()

        // Check if this is the current user
        val isMe = currentUserName != null && (
            name.equals(currentUserName, ignoreCase = true) ||
            item.namaLengkap?.equals(currentUserName, ignoreCase = true) == true
        )

        // Rank badge
        holder.binding.tvRank.text = "${position + 1}"
        val rankBg = when (position) {
            0 -> Color.parseColor("#FFD700")
            1 -> Color.parseColor("#C0C0C0")
            2 -> Color.parseColor("#CD7F32")
            else -> Color.parseColor("#444444")
        }
        holder.binding.tvRank.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(rankBg)
        }
        holder.binding.tvRank.setTextColor(if (position < 3) Color.parseColor("#111111") else Color.WHITE)

        // Avatar
        holder.binding.tvAvatar.text = name.take(1).uppercase()

        // Name + subtitle
        holder.binding.tvName.text = name.uppercase()
        holder.binding.tvSubtitle.text = item.subtitle ?: item.kodeCabang ?: item.namaCabang ?: ""

        // Score badge
        holder.binding.tvScore.text = "Rp ${formatShort(item.omzet ?: 0.0)}"
        val scoreBg = GradientDrawable().apply {
            cornerRadius = 8f * dp
            setColor(when (position) {
                0 -> Color.parseColor("#FFD700")
                1 -> Color.parseColor("#C0C0C0")
                2 -> Color.parseColor("#CD7F32")
                else -> Color.parseColor("#C1121F")
            })
        }
        holder.binding.tvScore.background = scoreBg
        holder.binding.tvScore.setTextColor(if (position < 3) Color.parseColor("#111111") else Color.WHITE)

        // Highlight current user row
        if (isMe) {
            val cardBg = GradientDrawable().apply {
                cornerRadius = 14f * dp
                setColor(Color.parseColor("#3DC1121F")) // semi-transparan merah
                setStroke((2f * dp).toInt(), Color.parseColor("#C1121F"))
            }
            (holder.binding.root as? com.google.android.material.card.MaterialCardView)?.apply {
                strokeWidth = (2f * dp).toInt()
                strokeColor = Color.parseColor("#C1121F")
                setCardBackgroundColor(Color.parseColor("#3A1A1A"))
            }
            holder.binding.tvName.setTypeface(null, Typeface.BOLD)
            holder.binding.tvSubtitle.text = "⭐ Posisi Anda"
            holder.binding.tvSubtitle.setTextColor(Color.parseColor("#FFD700"))
        } else {
            (holder.binding.root as? com.google.android.material.card.MaterialCardView)?.apply {
                strokeWidth = 0
                setCardBackgroundColor(Color.parseColor("#2A2A2A"))
            }
            holder.binding.tvSubtitle.setTextColor(Color.parseColor("#80FFFFFF"))
        }
    }

    private fun formatShort(value: Double): String {
        return when {
            value >= 1_000_000_000 -> String.format("%.1fM", value / 1_000_000_000)
            value >= 1_000_000 -> String.format("%.1fJt", value / 1_000_000)
            value >= 1_000 -> String.format("%.0fRb", value / 1_000)
            else -> value.toInt().toString()
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<LeaderboardItem>() {
            override fun areItemsTheSame(old: LeaderboardItem, new: LeaderboardItem) =
                old.id == new.id && old.nama == new.nama
            override fun areContentsTheSame(old: LeaderboardItem, new: LeaderboardItem) =
                old == new
        }
    }
}
