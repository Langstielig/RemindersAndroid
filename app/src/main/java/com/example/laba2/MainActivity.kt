package com.example.laba2

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
        isGranted: Boolean -> if(!isGranted)
        {
            Toast.makeText(this, R.string.permission_warning, Toast.LENGTH_LONG).show()
        }
    }

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        requestToEnableNotifications(alarmManager)
        createNotificationsForOldReminders(alarmManager)

        if(savedInstanceState == null)
        {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent != null)
        {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent)
    {
        val openFragment = intent.getStringExtra(FragmentKeys.OPEN_FRAGMENT)
        if(openFragment == "EditReminder")
        {
            val reminderId = intent.getIntExtra(FragmentKeys.REMINDER_ID, -1)
            if(reminderId != -1)
            {
                openEditReminderFragment(reminderId)
            }
        }
    }

    private fun openEditReminderFragment(reminderId: Int)
    {
        val repo = ReminderPrefsRepository(this)
        val reminders = repo.load()

        var editReminder = reminders[0] //??

        for(reminder in reminders)
        {
            if(reminder.id == reminderId)
            {
                editReminder = reminder
            }
        }

        val editReminderPosition = reminders.indexOf(editReminder)

        val bundle = Bundle().apply {
            putParcelable(FragmentKeys.REMINDER_KEY, editReminder)
            putInt(FragmentKeys.EDIT_POSITION, editReminderPosition)
        }

        Log.d(TAG, "Navigating to EditReminderFragment to edit reminder: $editReminder")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        navController.navigate(R.id.edit_reminder_fragment, bundle)
    }

    private fun requestToEnableNotifications(alarmManager: AlarmManager)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && !NotificationManagerCompat.from(this).areNotificationsEnabled())
        {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms())
        {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }

    private fun createNotificationsForOldReminders(alarmManager: AlarmManager)
    {
        val repo = ReminderPrefsRepository(this)
        val reminders = repo.load()

        for(reminder in reminders) {
            if(reminder.date != null && !Utils.isReminderInPast(reminder.date!!, reminder.time!!)) {
                Log.d(TAG, "Creating notification for reminder: $reminder")
                val dateTime = Utils.combineDateAndTime(reminder.date!!, reminder.time!!)
                val resultDateTime: Long = dateTime.time

                val pendingIntent = Utils.getPendingIntent(this, reminder.id, reminder.title, reminder.description)

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, resultDateTime, pendingIntent)
                }
            }
        }
    }
}