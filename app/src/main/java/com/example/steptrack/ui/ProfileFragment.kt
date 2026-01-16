package com.example.steptrack.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.steptrack.data.AppDatabase
import com.example.steptrack.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val dao = db.workoutDao()

        // Obserwowanie całkowitej liczby kroków
        dao.getTotalSteps().observe(viewLifecycleOwner) { total ->
            val steps = total ?: 0
            binding.totalStepsText.text = String.format("%, d", steps).replace(',', ' ')
        }

        // Obserwowanie całkowitego dystansu
        dao.getTotalDistance().observe(viewLifecycleOwner) { total ->
            val distanceKm = (total ?: 0.0) / 1000.0
            binding.totalDistanceText.text = String.format("%.2f km", distanceKm)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
