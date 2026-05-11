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
import java.text.SimpleDateFormat
import java.util.*

class SequenceAdapter(private val historyEntries: List<GameManager.HistoryEntry>) :
    RecyclerView.Adapter<SequenceAdapter.ViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()
    private val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val collapsedLayout: View = view.findViewById(R.id.collapsedLayout)
        val tvCount: TextView = view.findViewById(R.id.tvCount)
        val tvSequence: TextView = view.findViewById(R.id.tvSequence)

        val expandedLayout: View = view.findViewById(R.id.expandedLayout)
        val tvExpandedScore: TextView = view.findViewById(R.id.tvExpandedScore)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvRepetitions: TextView = view.findViewById(R.id.tvRepetitions)
        val tvFullSequence: TextView = view.findViewById(R.id.tvFullSequence)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sequence, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = historyEntries[position]
        val isExpanded = expandedPositions.contains(position)

        // collapsed version
        holder.tvCount.text = entry.score.toString()
        holder.tvSequence.text = createSequenceSpannable(entry)
        holder.collapsedLayout.visibility = if (isExpanded) View.GONE else View.VISIBLE

        // expanded version
        if (isExpanded) {
            holder.expandedLayout.visibility = View.VISIBLE
            holder.tvExpandedScore.text = entry.score.toString()
            holder.tvDateTime.text = if (entry.timestamp > 0) dateFormat.format(Date(entry.timestamp)) else "-"
            
            // set value ON/OFF
            val context = holder.itemView.context
            holder.tvRepetitions.text = if (entry.repetitionsAllowed) 
                context.getString(R.string.on) 
            else 
                context.getString(R.string.off)

            holder.tvFullSequence.text = createSequenceSpannable(entry)
        } else {
            holder.expandedLayout.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    private fun createSequenceSpannable(entry: GameManager.HistoryEntry): SpannableStringBuilder {
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
        return builder
    }

    override fun getItemCount() = historyEntries.size
}
