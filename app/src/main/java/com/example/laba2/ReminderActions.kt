package com.example.laba2

interface ReminderActions {
    fun onDelete(reminder: Reminder, position: Int)
    fun onEdit(reminder: Reminder, position: Int)
}