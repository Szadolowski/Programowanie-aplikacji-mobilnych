package com.example.steptrack.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.steptrack.R
import com.example.steptrack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StepViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)

        // Obsługa środkowego przycisku FAB
        binding.fabPlayPause.setOnClickListener {
            viewModel.toggleTracking()
            // Przeniesienie na zakładkę kroki, jeśli nie jesteśmy na niej
            if (navController.currentDestination?.id != R.id.navigation_tracker) {
                navController.navigate(R.id.navigation_tracker)
            }
        }

        // Obserwowanie stanu liczenia i aktualizacja ikony
        viewModel.isTracking.observe(this) { isTracking ->
            if (isTracking) {
                binding.fabPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                binding.fabPlayPause.setImageResource(android.R.drawable.ic_media_play)
            }
        }
    }
}
