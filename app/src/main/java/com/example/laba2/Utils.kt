package com.example.laba2

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.util.Date
import kotlin.math.roundToInt

class Utils {
    companion object
    {
        fun getID(): Int
        {
            return (Math.random() * 1000000).roundToInt()
        }

        fun isReminderInPast(date: Date, time: Date): Boolean
        {
            val dateTime = combineDateAndTime(date, time)

            val calendar = Calendar.getInstance()
            val now = calendar.time
            return dateTime.before(now)
        }

        fun getPendingIntent(context: Context, id:Int, title: String, description: String): PendingIntent
        {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra(FragmentKeys.NOTIFICATION_DESCRIPTION, description)
                putExtra(FragmentKeys.NOTIFICATION_TITLE, title)
                putExtra(FragmentKeys.NOTIFICATION_ID, id)
                //flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            return PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        fun combineDateAndTime(date: Date, time: Date) : Date
        {
            val calendarDate = Calendar.getInstance()
            calendarDate.time = date

            val calendarTime = Calendar.getInstance()
            calendarTime.time = time

            calendarDate[Calendar.HOUR_OF_DAY] = calendarTime[Calendar.HOUR_OF_DAY]
            calendarDate[Calendar.MINUTE] = calendarTime[Calendar.MINUTE]
            calendarDate[Calendar.SECOND] = calendarTime[Calendar.SECOND]
            calendarDate[Calendar.MILLISECOND] = calendarTime[Calendar.MILLISECOND]

            return calendarDate.time
        }

        fun showSnackbar(context: Context, view: View, messageId: Int)
        {
            Snackbar.make(
                view,
                context.getString(messageId),
                Snackbar.LENGTH_SHORT
            )
                .setBackgroundTint(Color.DKGRAY)
                .show()
        }
    }
}