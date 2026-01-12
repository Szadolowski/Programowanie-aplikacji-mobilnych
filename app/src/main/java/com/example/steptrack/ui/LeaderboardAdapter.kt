package com.example.steptrack.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steptrack.data.DayWithWorkouts
import com.example.steptrack.data.Workout
import com.example.steptrack.databinding.ItemLeaderboardBinding
import com.example.steptrack.databinding.ItemWorkoutDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class LeaderboardAdapter(private var entries: List<DayWithWorkouts>) :
    RecyclerView.Adapter<LeaderboardAdapter.ParentViewHolder>() {

    class ParentViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val entry = entries[position]
        val day = entry.day
        val workouts = entry.workouts

        // 1. Obliczanie sumy kroków ze wszystkich treningów dla danego dnia
        val totalStepsFromWorkouts = workouts.sumOf { it.steps }
        
        // 2. Obliczanie całkowitego dystansu ze wszystkich treningów
        val totalDistanceMetres = workouts.sumOf { it.distanceMetres }
        val totalDistanceKm = totalDistanceMetres / 1000.0

        with(holder.binding) {
            dateText.text = day.date
            
            // 3. Wyświetlanie wyliczonej sumy w nagłówku
            stepsCountText.text = String.format("%, d", totalStepsFromWorkouts).replace(',', ' ')
            distanceText.text = String.format("%.2f km", totalDistanceKm)

            // Logika rozwijania (Expandable)
            workoutDetailsRecyclerView.visibility = View.GONE
            
            workoutDetailsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            workoutDetailsRecyclerView.adapter = WorkoutDetailAdapter(workouts)

            root.setOnClickListener {
                val visibility = if (workoutDetailsRecyclerView.visibility == View.GONE) View.VISIBLE else View.GONE
                workoutDetailsRecyclerView.visibility = visibility
            }
        }
    }

    override fun getItemCount() = entries.size

    fun updateData(newEntries: List<DayWithWorkouts>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}

class WorkoutDetailAdapter(private val workouts: List<Workout>) :
    RecyclerView.Adapter<WorkoutDetailAdapter.ChildViewHolder>() {

    class ChildViewHolder(val binding: ItemWorkoutDetailBinding) : RecyclerView.ViewHolder(binding.root)

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val binding = ItemWorkoutDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val workout = workouts[position]
        with(holder.binding) {
            startTimeText.text = timeFormat.format(Date(workout.startTime))
            val km = workout.distanceMetres / 1000.0
            workoutStatsText.text = String.format("%.2f km • %d kroków", km, workout.steps)
        }
    }

    override fun getItemCount() = workouts.size
}
