package com.example.laba2

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ReminderListFragment : Fragment(R.layout.fragment_reminder_list), ReminderActions {

    private val TAG = "ReminderList"

    private lateinit var repo: ReminderPrefsRepository
    private lateinit var adapter: ReminderAdapter
    private lateinit var alarmManager: AlarmManager
    private val reminders = mutableListOf<Reminder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ReminderListFragment onViewCreated")

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        repo = ReminderPrefsRepository(requireContext())

        adapter = ReminderAdapter(reminders, this)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = this@ReminderListFragment.adapter

        parentFragmentManager.setFragmentResultListener(FragmentKeys.EDIT_RESULT, viewLifecycleOwner) { _, bundle ->
            val updated = bundle.getParcelable<Reminder>(FragmentKeys.UPDATED_REMINDER) ?: run {
                Log.d(TAG, "No updated reminder found in bundle")
                return@setFragmentResultListener
            }
            val pos = bundle.getInt(FragmentKeys.POSITION, -1)
            Log.d(TAG, "Received updated reminder: $updated at position $pos")
            if(pos != -1)
            {
                reminders[pos] = updated
                adapter.notifyItemChanged(pos)
                repo.update(pos, updated)

                Log.d(TAG, "Updated reminder at position $pos in list")
            }
        }

        parentFragmentManager.setFragmentResultListener(FragmentKeys.ADD_RESULT, viewLifecycleOwner) { _, bundle ->
            val newReminder = bundle.getParcelable<Reminder>(FragmentKeys.NEW_REMINDER) ?: run {
                Log.d(TAG, "no new reminder found in bundle")
                return@setFragmentResultListener
            }

            Log.d(TAG, "Received new reminder: $newReminder")
            reminders.add(0, newReminder)
            adapter.notifyItemInserted(0)
            repo.add(newReminder)

            Log.d(TAG, "New reminder added at the top of the list")
        }

        LoadReminders()

        val navController = view.findNavController()

        val addReminderButton = view.findViewById<FloatingActionButton>(R.id.add_reminder_button)
        val clearButton = view.findViewById<MaterialButton>(R.id.delete_all_reminders_button)

        addReminderButton.setOnClickListener {
            Log.d(TAG, "Navigating to EditReminderFragment to add new reminder")
            navController.navigate(R.id.action_global_to_edit)
        }

        clearButton.setOnClickListener {
            ClearAllReminders()
            Utils.showSnackbar(requireContext(), requireView(), R.string.delete_all_reminders_snackbar)
        }
    }

    override fun onDelete(reminder: Reminder, position: Int)
    {
        reminders.removeAt(position)
        adapter.notifyItemRemoved(position)
        repo.save(reminders)
        alarmManager.cancel(Utils.getPendingIntent(requireContext(), reminder.id, reminder.title, reminder.description))

        Utils.showSnackbar(requireContext(), requireView(), R.string.delete_reminder_snackbar)
    }

    override fun onEdit(reminder: Reminder, position: Int) {
        val bundle = Bundle().apply {
            putParcelable(FragmentKeys.REMINDER_KEY, reminder)
            putInt(FragmentKeys.EDIT_POSITION, position)
        }

        Log.d(TAG, "Navigating to EditReminderFragment to edit reminder: $reminder")
        view?.findNavController()?.navigate(
            R.id.action_reminder_list_fragment_to_edit_list_fragment,
            bundle
        )
    }

    private fun LoadReminders()
    {
        val loadedReminders = repo.load()
        Log.d(TAG, "Loaded reminders from SharedPreferences: $loadedReminders")
        reminders.clear()
        reminders.addAll(loadedReminders)
        Log.d(TAG, "Reminders list is: $reminders")
    }

    private fun ClearAllReminders()
    {
        Log.d(TAG, "Try to remove all reminders")
        for(reminder in reminders)
        {
            alarmManager.cancel(Utils.getPendingIntent(requireContext(), reminder.id, reminder.title, reminder.description))
        }
        val remindersCount = repo.getCount()
        repo.clear()
        reminders.clear() //?
        adapter.notifyItemRangeRemoved(0, remindersCount)
    }
}