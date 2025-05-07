package com.example.laba2

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val description = intent?.getStringExtra(FragmentKeys.NOTIFICATION_DESCRIPTION) ?: ""
        val title = intent?.getStringExtra(FragmentKeys.NOTIFICATION_TITLE) ?: ""
        val id = intent?.getIntExtra(FragmentKeys.NOTIFICATION_ID, 0)
        val notificationManager = NotificationManagerCompat.from(context)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(FragmentKeys.OPEN_FRAGMENT, "EditReminder")
            putExtra(FragmentKeys.REMINDER_ID, id)
        }

        val pendingIntent = id?.let {
            PendingIntent.getActivity(
                context,
                it,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(context, ReminderApplication.channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority((NotificationCompat.PRIORITY_HIGH))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        if(ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
        {
            return
        }
        notificationManager.notify(id!!, builder.build())
    }
}