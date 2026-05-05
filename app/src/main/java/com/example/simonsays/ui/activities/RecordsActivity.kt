package com.example.simonsays.ui.activities

import com.example.simonsays.R
import com.example.simonsays.ui.adapters.SequenceAdapter

import android.os.Bundle
import android.view.View
import android.widget.TextView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecordsActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTop1: TextView
    private lateinit var tvTop2: TextView
    private lateinit var tvTop3: TextView
    private lateinit var topScoresContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)
        setupMenuButtons()

        recyclerView = findViewById(R.id.rvHistory)
        tvTop1 = findViewById(R.id.tvTop1)
        tvTop2 = findViewById(R.id.tvTop2)
        tvTop3 = findViewById(R.id.tvTop3)
        topScoresContainer = findViewById(R.id.topScoresContainer)

        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // called when resuming the activity
    override fun onResume() {
        super.onResume()
        refreshHistory()
    }

    // reloads the history
    private fun refreshHistory() {
        val historyEntries = gameManager.getAllSequences()
        
        // update top 3 scores based on the score field
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

        // update entire list
        recyclerView.adapter = SequenceAdapter(historyEntries)
    }
}
