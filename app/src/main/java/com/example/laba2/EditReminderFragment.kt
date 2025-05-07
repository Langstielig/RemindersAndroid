package com.example.laba2

import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditReminderFragment : Fragment(R.layout.fragment_edit_reminder) {
    private val TAG = "EditReminder"
    private var editingPos: Int? = null

    private lateinit var alarmManager: AlarmManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val fragmentTitle = view.findViewById<TextView>(R.id.edit_reminder_title)
        val backButton = view.findViewById<Button>(R.id.back_button)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        val titleInput = view.findViewById<EditText>(R.id.title_input)
        val descriptionInput = view.findViewById<EditText>(R.id.description_input)
        val dateInput = view.findViewById<EditText>(R.id.date_input)
        val timeInput = view.findViewById<EditText>(R.id.time_input)

        var editedReminderID: Int? = null

        arguments?.getParcelable<Reminder>(FragmentKeys.REMINDER_KEY)?.let { rem ->
            editingPos = requireArguments().getInt(FragmentKeys.EDIT_POSITION, -1)
            editedReminderID = rem.id

            FillInputFields(rem ,fragmentTitle, titleInput, descriptionInput, dateInput, timeInput)
        }

        dateInput.setOnClickListener {
            ShowDatePicker(dateInput)
        }

        timeInput.setOnClickListener {
            ShowTimePicker(timeInput)
        }

        backButton.setOnClickListener {
            ToReminderListFragment()
        }

        saveButton.setOnClickListener {
            val isFilled = TitleFillCheck(titleInput) && DateAndTimeFillCheck(dateInput, timeInput)
            if(isFilled) {
                val reminder = CreateReminder(titleInput, descriptionInput, dateInput, timeInput, editedReminderID)
                Log.d(TAG, "Created reminder: $reminder")

                val editingPos = arguments?.getInt(FragmentKeys.EDIT_POSITION, -1)
                val navController = view.findNavController()

                if(editingPos != null && editingPos != -1)
                {
                    Log.d(TAG, "Editing reminder at position: $editingPos reminder is $reminder")

                    if(reminder.date != null && !Utils.isReminderInPast(reminder.date!!, reminder.time!!)) {
                        alarmManager.cancel(Utils.getPendingIntent(requireContext(), reminder.id, reminder.title, reminder.description))
                        ScheduleNotification(requireContext(), reminder)
                    }

                    Utils.showSnackbar(requireContext(), requireView(), R.string.edit_reminder_snackbar)

                    val result = Bundle().apply {
                        putParcelable(FragmentKeys.UPDATED_REMINDER, reminder)
                        putInt(FragmentKeys.POSITION, editingPos)
                    }
                    parentFragmentManager.setFragmentResult(FragmentKeys.EDIT_RESULT, result)

                    Log.d(TAG, "Updated reminder, returning to ReminderListFragment")
                    navController.popBackStack()
                }
                else
                {
                    Log.d(TAG, "Adding new reminder, reminder is $reminder")

                    if(reminder.date != null) {
                        ScheduleNotification(requireContext(), reminder)
                    }

                    Utils.showSnackbar(requireContext(), requireView(), R.string.add_reminder_snackbar)

                    val result = Bundle().apply {
                        putParcelable(FragmentKeys.NEW_REMINDER, reminder)
                    }
                    parentFragmentManager.setFragmentResult(FragmentKeys.ADD_RESULT, result)

                    Log.d(TAG, "New reminder added, returning to ReminderListFragment")
                    navController.popBackStack()
                }

                val bundle = Bundle().apply {
                    putParcelable(FragmentKeys.REMINDER_KEY, reminder)
                }
            }
        }
    }

    private fun ShowDatePicker(dateInput: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.MyDatePicker)
            .setTitleText(R.string.choose_date_title)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selection
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            dateInput.setText(format.format(calendar.time))
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun ShowTimePicker(timeInput: EditText)
    {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            requireContext(),
            R.style.MyDatePicker,
            { _, hourOfDay, minute ->
                val timeStr = "%02d:%02d".format(hourOfDay, minute)
                timeInput.setText(timeStr)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePicker.show()
    }

    private fun DateAndTimeFillCheck(dateInput: EditText, timeInput: EditText): Boolean
    {
        if(timeInput.text.toString() != "" && dateInput.text.toString() == "")
        {
            dateInput.setError(getString(R.string.date_input_error))
            return false
        }
        return true
    }

    private fun TitleFillCheck(titleInput: EditText): Boolean
    {
        val title = titleInput.text.toString().trim()
        if(title == "")
        {
            titleInput.setError(getString(R.string.title_input_error))
            return false
        }
        return true
    }

    private fun CreateReminder(titleInput : EditText, descriptionInput: EditText, dateInput: EditText, timeInput: EditText, editedReminderID: Int?): Reminder
    {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        val date: Date?
        if(dateInput.text.toString() != "") {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            date = dateFormat.parse(dateInput.text.toString().trim())
        }
        else
        {
            date = null
        }

        val time: Date?
        if(timeInput.text.toString() != "") {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            time = timeFormat.parse(timeInput.text.toString().trim())
        }
        else if(date != null)
        {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            time = calendar.time
        }
        else
        {
            time = null
        }
        val id: Int = editedReminderID ?: Utils.getID()
        val reminder = Reminder(title, description, date, time, id)

        return reminder
    }

    private fun ScheduleNotification(context: Context, reminder: Reminder)
    {
        val dateTime = Utils.combineDateAndTime(reminder.date!!, reminder.time!!)
        val resultDateTime: Long = dateTime.time

        val pendingIntent = Utils.getPendingIntent(context, reminder.id, reminder.title, reminder.description)

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms())
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, resultDateTime, pendingIntent)
        }
    }

    private fun ToReminderListFragment()
    {
        findNavController().navigate(R.id.action_edit_list_fragment_to_reminder_list_fragment)
    }

    private fun FillInputFields(reminder: Reminder, fragmentTitle: TextView, titleInput: EditText, descriptionInput: EditText, dateInput: EditText, timeInput: EditText)
    {
        fragmentTitle.text = getString(R.string.edit_reminder_title)

        titleInput.setText(reminder.title)
        descriptionInput.setText(reminder.description)

        if(reminder.date != null) {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            dateInput.setText(format.format(reminder.date))
        }

        if(reminder.time != null) {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeInput.setText(format.format(reminder.time))
        }
    }
}