package com.example.simonsays.ui.activities

import com.example.simonsays.R
import com.example.simonsays.ui.adapters.SequenceAdapter

import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecordsActivity : BaseActivity() {

    // start of the Activity (recycler view of all sequences)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)
        setupMenuButtons()

        val recyclerView = findViewById<RecyclerView>(R.id.rvHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        val sequences = gameManager.getAllSequences()
        recyclerView.adapter = SequenceAdapter(sequences)
    }
}
