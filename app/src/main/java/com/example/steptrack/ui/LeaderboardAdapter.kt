package com.example.steptrack.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.steptrack.databinding.ItemLeaderboardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryEntry(val date: Long, val steps: Int, val distanceKm: Double)

class LeaderboardAdapter(private val entries: List<HistoryEntry>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        with(holder.binding) {
            dateText.text = dateFormat.format(Date(entry.date))
            distanceText.text = String.format("%.2f km", entry.distanceKm)
            stepsCountText.text = String.format("%, d", entry.steps).replace(',', ' ')
        }
    }

    override fun getItemCount() = entries.size
}
