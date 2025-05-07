package com.example.laba2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ReminderAdapter(
    private val reminders: MutableList<Reminder>,
    private val actions: ReminderActions
) :RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private val TAG = "ReminderAdapter"

    inner class ReminderViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        private val titleTextView: TextView = view.findViewById(R.id.reminder_title)
        private val descriptionTextView: TextView = view.findViewById(R.id.description_text)
        private val dateTextView: TextView = view.findViewById(R.id.date_text)
        private val timeTextView: TextView = view.findViewById(R.id.time_text)
        private val editButton: ImageButton = view.findViewById(R.id.edit_button)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_button)

        fun bind(reminder: Reminder, pos: Int)
        {
            titleTextView.text = reminder.title
            descriptionTextView.text = reminder.description

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val dateStr = reminder.date?.let { dateFormat.format(it) } ?: ""
            dateTextView.text = dateStr

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeStr = reminder.time?.let { timeFormat.format(it) } ?: ""
            timeTextView.text = timeStr

            deleteButton.setOnClickListener {
                actions.onDelete(reminder, pos)
            }

            editButton.setOnClickListener {
                actions.onEdit(reminders[pos], pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view)
    }

    override fun getItemCount(): Int = reminders.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        Log.d(TAG, "Binding reminder at position $position: $reminder")

        holder.bind(reminders[position], position)
    }
}