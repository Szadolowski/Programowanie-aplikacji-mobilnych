package com.example.steptrack.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.steptrack.databinding.FragmentTrackerBinding

class TrackerFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentTrackerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StepViewModel by activityViewModels()

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    
    // Wartość czujnika w momencie kliknięcia "Start"
    private var initialStepCount = -1f 
    private val dailyGoal = 10000

    // Rejestrator prośby o uprawnienia (wymagane od Android 10)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupStepCounter()
        } else {
            Toast.makeText(requireContext(), "Brak uprawnień do liczenia kroków", Toast.LENGTH_SHORT).show()
        }
    }

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
        binding.stepsProgressBar.max = dailyGoal

        checkPermissionsAndSetup()

        binding.startButton.setOnClickListener {
            if (!viewModel.isTracking.value!!) {
                // Przy starcie resetujemy bazową wartość czujnika
                initialStepCount = -1f 
            }
            viewModel.toggleTracking()
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

    private fun checkPermissionsAndSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    setupStepCounter()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        } else {
            setupStepCounter()
        }
    }

    private fun setupStepCounter() {
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor == null) {
            Toast.makeText(requireContext(), "Czujnik kroków nie jest dostępny na tym urządzeniu", Toast.LENGTH_LONG).show()
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
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun unregisterSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceBoot = event.values[0]

            if (initialStepCount == -1f) {
                // Pierwszy odczyt po kliknięciu "Start" staje się naszym punktem odniesienia
                initialStepCount = totalStepsSinceBoot
            }

            // Obliczamy kroki zrobione tylko w tej sesji
            val currentSessionSteps = (totalStepsSinceBoot - initialStepCount).toInt()
            
            // Możemy tutaj albo nadpisywać wynik, albo dodawać do istniejących 
            // W tej implementacji aktualizujemy wynik sesji
            if (viewModel.isTracking.value == true) {
                // Aktualizujemy kroki w ViewModelu
                // Zakładamy, że chcemy widzieć postęp od momentu kliknięcia Start
                viewModel.setSteps(currentSessionSteps)
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
        // Nie wyłączamy całkowicie, aby sensor mógł pracować w tle (jeśli to byłaby usługa)
        // Ale we fragmencie odpinamy listenera
        unregisterSensor()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
