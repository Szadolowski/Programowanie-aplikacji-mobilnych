package com.example.steptrack.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.steptrack.databinding.FragmentMapBinding
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Zmienna do przechowywania ostatniej znanej pozycji użytkownika
    private var lastKnownLocation: Point? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            setupLocationComponent()
        } else {
            Toast.makeText(requireContext(), "Brak uprawnień do lokalizacji", Toast.LENGTH_SHORT).show()
        }
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener { point ->
        lastKnownLocation = point // Zapisujemy pozycję
        binding.mapView.mapboxMap.setCamera(CameraOptions.Builder().center(point).build())
        binding.mapView.gestures.focalPoint = binding.mapView.mapboxMap.pixelForCoordinate(point)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener { bearing ->
        binding.mapView.mapboxMap.setCamera(CameraOptions.Builder().bearing(bearing).build())
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }
        override fun onMove(detector: MoveGestureDetector): Boolean = false
        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
            checkPermissions()
        }

        binding.fabRecenter.setOnClickListener {
            recenterCamera()
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
            == PackageManager.PERMISSION_GRANTED) {
            setupLocationComponent()
        } else {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun setupLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            enabled = true
            pulsingEnabled = true
        }

        startTracking()
        binding.mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(15.0).build())
    }

    private fun startTracking() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun recenterCamera() {
        // Ponownie włączamy śledzenie
        startTracking()
        
        // Wykorzystujemy zapisaną ostatnią pozycję
        lastKnownLocation?.let { point ->
            binding.mapView.mapboxMap.flyTo(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(18.0)
                    .build()
            )
        }
    }

    private fun onCameraTrackingDismissed() {
        binding.mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onCameraTrackingDismissed()
        _binding = null
    }
}
