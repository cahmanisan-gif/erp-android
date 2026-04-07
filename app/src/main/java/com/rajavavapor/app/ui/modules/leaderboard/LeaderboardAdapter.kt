package com.rajavavapor.app.ui.modules.leaderboard

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rajavavapor.app.data.LeaderboardItem
import com.rajavavapor.app.databinding.ItemLeaderboardBinding
import java.text.NumberFormat
import java.util.Locale

class LeaderboardAdapter :
    ListAdapter<LeaderboardItem, LeaderboardAdapter.ViewHolder>(DIFF) {

    class ViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val fmt = NumberFormat.getInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

        // Rank
        holder.binding.tvRank.text = "${position + 1}"

        // Top 3 special colors
        val rankBg = when (position) {
            0 -> Color.parseColor("#FFD700") // Gold
            1 -> Color.parseColor("#C0C0C0") // Silver
            2 -> Color.parseColor("#CD7F32") // Bronze
            else -> Color.parseColor("#444444")
        }
        holder.binding.tvRank.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(rankBg)
        }
        holder.binding.tvRank.setTextColor(if (position < 3) Color.parseColor("#111111") else Color.WHITE)

        // Avatar
        val name = item.getDisplayName()
        holder.binding.tvAvatar.text = name.take(1).uppercase()

        // Name + subtitle
        holder.binding.tvName.text = name.uppercase()
        holder.binding.tvSubtitle.text = item.subtitle ?: item.kodeCabang ?: ""

        // Score badge
        holder.binding.tvScore.text = "Rp ${formatShort(item.omzet ?: 0.0)}"

        // Score badge color per rank
        val scoreBg = GradientDrawable().apply {
            cornerRadius = 8f * holder.binding.root.context.resources.displayMetrics.density
            setColor(when (position) {
                0 -> Color.parseColor("#FFD700")
                1 -> Color.parseColor("#C0C0C0")
                2 -> Color.parseColor("#CD7F32")
                else -> Color.parseColor("#C1121F")
            })
        }
        holder.binding.tvScore.background = scoreBg
        holder.binding.tvScore.setTextColor(if (position < 3) Color.parseColor("#111111") else Color.WHITE)
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
                old.id == new.id
            override fun areContentsTheSame(old: LeaderboardItem, new: LeaderboardItem) =
                old == new
        }
    }
}
