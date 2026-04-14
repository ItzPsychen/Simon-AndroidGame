package com.example.simonsays.ui.adapters

import com.example.simonsays.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

class SequenceAdapter(private val sequences: List<List<Pair<String, Int>>>) :
    RecyclerView.Adapter<SequenceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCount: TextView = view.findViewById(R.id.tvCount)
        val tvSequence: TextView = view.findViewById(R.id.tvSequence)
    }

    // creates the layout for the sequence
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sequence, parent, false)
        return ViewHolder(view)
    }

    // creates the sequence to be later viewed
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sequence = sequences[position]
        holder.tvCount.text = sequence.size.toString()
        holder.tvSequence.text = sequence.joinToString(", ") { it.first }
    }

    override fun getItemCount() = sequences.size
}
