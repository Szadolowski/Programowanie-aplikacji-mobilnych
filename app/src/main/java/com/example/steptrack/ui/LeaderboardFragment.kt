package com.example.steptrack.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.steptrack.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

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

        // Aktualizacja tytułu na "Twoja Historia"
        binding.headerTitle.text = "Twoja Historia"

        // Przykładowe dane historyczne (offline)
        // Dystans liczony w przybliżeniu (np. 1 krok = 0.75m)
        val historyEntries = listOf(
            HistoryEntry(System.currentTimeMillis(), 8500, 6.37),
            HistoryEntry(System.currentTimeMillis() - 86400000, 12300, 9.22),
            HistoryEntry(System.currentTimeMillis() - 172800000, 5100, 3.82),
            HistoryEntry(System.currentTimeMillis() - 259200000, 15420, 11.56),
            HistoryEntry(System.currentTimeMillis() - 345600000, 7200, 5.40),
            HistoryEntry(System.currentTimeMillis() - 432000000, 6800, 5.10),
            HistoryEntry(System.currentTimeMillis() - 518400000, 10200, 7.65)
        )

        val adapter = LeaderboardAdapter(historyEntries)
        binding.leaderboardRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
