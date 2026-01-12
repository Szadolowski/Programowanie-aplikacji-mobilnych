package com.example.steptrack.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.steptrack.data.AppDatabase
import com.example.steptrack.data.DailySummary
import com.example.steptrack.data.DayWithWorkouts
import com.example.steptrack.data.Workout
import com.example.steptrack.databinding.FragmentLeaderboardBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private var currentData: List<DayWithWorkouts> = emptyList()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.headerTitle.text = "Wyniki"

        val adapter = LeaderboardAdapter(emptyList())
        binding.leaderboardRecyclerView.adapter = adapter

        val db = AppDatabase.getDatabase(requireContext())
        val workoutDao = db.workoutDao()

        binding.sortChipGroup.setOnCheckedStateChangeListener { _, _ ->
            applySortingAndRefresh(adapter)
        }

        lifecycleScope.launch {
            // WAŻNE: Seedowanie danych tylko jeśli baza jest pusta
            workoutDao.getAllDailySummaries().observe(viewLifecycleOwner) { summaries ->
                if (summaries.isEmpty()) {
                    lifecycleScope.launch { seedDatabase(db) }
                }
            }
            
            workoutDao.getAllDaysWithWorkouts().observe(viewLifecycleOwner) { dayWithWorkouts ->
                currentData = dayWithWorkouts.filter { it.workouts.sumOf { workout -> workout.steps } > 0 }
                applySortingAndRefresh(adapter)
            }
        }
    }

    private fun applySortingAndRefresh(adapter: LeaderboardAdapter) {
        val sortedList = if (binding.chipSortSteps.isChecked) {
            currentData.sortedByDescending { it.workouts.sumOf { w -> w.steps } }
        } else {
            currentData.sortedByDescending { 
                try {
                    dateFormatter.parse(it.day.date)
                } catch (e: Exception) {
                    Date(0)
                }
            }
        }
        adapter.updateData(sortedList)
    }

    private suspend fun seedDatabase(db: AppDatabase) {
        val workoutDao = db.workoutDao()
        
        val date1 = "22.05.2024"
        workoutDao.insertDailySummary(DailySummary(date1, 0))
        workoutDao.insertWorkout(Workout(
            parentDate = date1,
            startTime = 1716364800000,
            endTime = 1716368400000,
            steps = 4200,
            distanceMetres = 3100.0,
            routePointsJson = "[]"
        ))
        
        val date2 = "21.05.2024"
        workoutDao.insertDailySummary(DailySummary(date2, 0))
        workoutDao.insertWorkout(Workout(
            parentDate = date2,
            startTime = 1716282000000,
            endTime = 1716285600000,
            steps = 6100,
            distanceMetres = 4500.0,
            routePointsJson = "[]"
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
