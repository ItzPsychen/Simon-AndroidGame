package com.example.simonsays.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.simonsays.R
import com.example.simonsays.ui.adapters.SequenceAdapter

import kotlinx.coroutines.launch

class RecordsActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTop1: TextView
    private lateinit var tvTop2: TextView
    private lateinit var tvTop3: TextView
    private lateinit var recentGamesLabel: TextView
    private lateinit var topScoresContainer: View
    private lateinit var emptyStateLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)
        setupMenuButtons()

        recyclerView = findViewById(R.id.rvHistory)
        tvTop1 = findViewById(R.id.tvTop1)
        tvTop2 = findViewById(R.id.tvTop2)
        tvTop3 = findViewById(R.id.tvTop3)
        recentGamesLabel = findViewById(R.id.tvHistoryLabel)
        topScoresContainer = findViewById(R.id.topScoresContainer)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)

        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        refreshHistory()
    }

    // refresh the history
    private fun refreshHistory() {
        lifecycleScope.launch {
            val historyEntries = gameManager.getAllSequences()

            if (historyEntries.isEmpty()) {
                topScoresContainer.visibility = View.GONE
                recentGamesLabel.visibility = View.GONE
                recyclerView.visibility = View.GONE
                emptyStateLayout.visibility = View.VISIBLE
            } else {
                emptyStateLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recentGamesLabel.visibility = View.VISIBLE

                val topScores = historyEntries
                    .map { it.score }
                    .sortedDescending()
                    .take(3)

                if (topScores.isEmpty()) {
                    topScoresContainer.visibility = View.GONE
                } else {
                    topScoresContainer.visibility = View.VISIBLE
                    tvTop1.text = topScores.getOrNull(0)?.toString() ?: "-"
                    tvTop2.text = topScores.getOrNull(1)?.toString() ?: "-"
                    tvTop3.text = topScores.getOrNull(2)?.toString() ?: "-"
                }
            }

            recyclerView.adapter = SequenceAdapter(historyEntries)
        }
    }
}
