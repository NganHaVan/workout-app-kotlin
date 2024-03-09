package com.janet.workoutapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.janet.workoutapp.databinding.HistoryItemBinding

class HistoryItemAdapter(
    private val items: ArrayList<String>
): RecyclerView.Adapter<HistoryItemAdapter.ViewHolder>() {
    class ViewHolder(historyItemBinding: HistoryItemBinding): RecyclerView.ViewHolder(historyItemBinding.root) {
        val itemContainer = historyItemBinding.llHistoryItemContainer
        val date = historyItemBinding.tvItem
        val position = historyItemBinding.tvPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = items.get(position)
        holder.position.text = (position + 1).toString()
        holder.date.text = date

        if (position % 2 == 0) {
            holder.itemContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.superLightGrey))
        } else {
            holder.itemContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }
    }
}
