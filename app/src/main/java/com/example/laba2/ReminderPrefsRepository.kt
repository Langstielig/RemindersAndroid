package com.example.laba2

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReminderPrefsRepository(context: Context) {

    private val prefs = context.getSharedPreferences(FragmentKeys.PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val gson  = Gson()
    private val type  = object : TypeToken<MutableList<Reminder>>() {}.type

    fun load(): MutableList<Reminder> {
        val json = prefs.getString(FragmentKeys.REMINDERS_LIST, "[]") ?: "[]"
        return gson.fromJson(json, type)
    }

    fun save(list: List<Reminder>) {
        val json = gson.toJson(list)
        prefs.edit().putString(FragmentKeys.REMINDERS_LIST, json).apply()
    }

    fun add(reminder: Reminder) {
        val list = load()
        list.add(0, reminder)
        save(list)
    }

    fun update(position: Int, reminder: Reminder) {
        val list = load()
        if (position in list.indices) {
            list[position] = reminder
            save(list)
        }
    }

    fun clear() = prefs.edit().clear().apply()

    fun getCount(): Int
    {
        val list = load()
        return list.size
    }
}