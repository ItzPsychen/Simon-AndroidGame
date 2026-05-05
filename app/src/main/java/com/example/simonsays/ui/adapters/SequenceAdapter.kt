package com.example.simonsays.ui.adapters

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.example.simonsays.R
import com.example.simonsays.logic.GameManager

class SequenceAdapter(private val historyEntries: List<GameManager.HistoryEntry>) :
    RecyclerView.Adapter<SequenceAdapter.ViewHolder>() {

    // ViewHolder class (count + sequence)
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCount: TextView = view.findViewById(R.id.tvCount)
        val tvSequence: TextView = view.findViewById(R.id.tvSequence)
    }

    // create new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sequence, parent, false)
        return ViewHolder(view)
    }

    // bind information to ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = historyEntries[position]
        holder.tvCount.text = entry.score.toString()
        
        val builder = SpannableStringBuilder()
        entry.sequence.forEachIndexed { index, element ->
            val start = builder.length
            builder.append(element.label)
            if (element.isError) {
                builder.setSpan(
                    ForegroundColorSpan(Color.RED),
                    start,
                    builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (index < entry.sequence.size - 1) {
                builder.append(", ")
            }
        }
        holder.tvSequence.text = builder
    }

    // returns the size of the list
    override fun getItemCount() = historyEntries.size
}
