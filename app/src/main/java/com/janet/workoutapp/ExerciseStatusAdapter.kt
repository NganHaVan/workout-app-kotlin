package com.janet.workoutapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.janet.workoutapp.databinding.ItemExerciseStatusBinding

class ExerciseStatusAdapter(val exerciseList: ArrayList<ExerciseModel>):
    RecyclerView.Adapter<ExerciseStatusAdapter.ExerciseStatusViewHolder>() {

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ExerciseStatusViewHolder(binding: ItemExerciseStatusBinding): RecyclerView.ViewHolder(binding.root) {
        val tvItem = binding.tvItem
    }

    /**
     * Inflates the item view which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseStatusViewHolder {
        return ExerciseStatusViewHolder(ItemExerciseStatusBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ExerciseStatusViewHolder, position: Int) {
        val currentExercise = exerciseList[position]
        holder.tvItem.text = currentExercise.getId().toString()

        when {
            currentExercise.getIsSelected() -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_circular_thin_accent_border)
                holder.tvItem.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_color))
            }
            currentExercise.getIsCompleted() -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_circular_accent_color_bg)
                holder.tvItem.setTextColor(Color.WHITE)
            }
            else -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_circular_gray_bg)
                holder.tvItem.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.text_color))
            }
        }
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }
}
