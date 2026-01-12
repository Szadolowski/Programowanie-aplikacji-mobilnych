package com.example.steptrack.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.steptrack.data.AppDatabase
import com.example.steptrack.data.DailySummary
import com.example.steptrack.data.Workout
import com.example.steptrack.databinding.FragmentTrackerBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class TrackerFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentTrackerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StepViewModel by activityViewModels()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    
    private var magnitudePrevious = 0.0
    private val stepThreshold = 6.0
    private val dailyGoal = 10000

    private var workoutStartTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        binding.stepsProgressBar.max = dailyGoal

        binding.startButton.setOnClickListener {
            if (viewModel.isTracking.value == true) {
                saveWorkoutAndStop()
            } else {
                startNewWorkout()
            }
        }

        viewModel.stepCount.observe(viewLifecycleOwner) { steps ->
            binding.stepsTextView.text = steps.toString()
            binding.stepsProgressBar.setProgress(steps, true)
        }

        viewModel.isTracking.observe(viewLifecycleOwner) { isTracking ->
            updateUI(isTracking)
            if (isTracking) {
                registerSensor()
            } else {
                unregisterSensor()
            }
        }
    }

    private fun startNewWorkout() {
        workoutStartTime = System.currentTimeMillis()
        viewModel.resetSteps()
        viewModel.setTracking(true)
    }

    private fun saveWorkoutAndStop() {
        val endTime = System.currentTimeMillis()
        val steps = viewModel.stepCount.value ?: 0
        val distance = (steps * 0.7)
        
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date(workoutStartTime))

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val dao = db.workoutDao()

            // Dodajemy lub aktualizujemy wpis dzienny
            dao.insertDailySummary(DailySummary(dateStr, 0))

            // Tworzymy NOWY unikalny trening
            val workout = Workout(
                id = 0, // Auto-generowanie klucza głównego
                parentDate = dateStr,
                startTime = workoutStartTime,
                endTime = endTime,
                steps = steps,
                distanceMetres = distance,
                routePointsJson = "[]"
            )
            dao.insertWorkout(workout)
            
            viewModel.setTracking(false)
        }
    }

    private fun updateUI(isTracking: Boolean) {
        if (isTracking) {
            binding.startButton.text = "Stop"
            binding.startButton.setIconResource(android.R.drawable.ic_media_pause)
        } else {
            binding.startButton.text = "Start"
            binding.startButton.setIconResource(android.R.drawable.ic_media_play)
        }
    }

    private fun registerSensor() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun unregisterSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val magnitude = sqrt((x * x + y * y + z * z).toDouble())
            val magnitudeDelta = magnitude - magnitudePrevious
            magnitudePrevious = magnitude

            if (magnitudeDelta > stepThreshold) {
                if (viewModel.isTracking.value == true) {
                    viewModel.addStep()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        if (viewModel.isTracking.value == true) {
            registerSensor()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterSensor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
