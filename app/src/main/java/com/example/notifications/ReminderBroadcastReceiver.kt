package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.BusinessApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == BackgroundReminderManager.ACTION_REMINDER_ALARM) {
            val reminderId = intent.getLongExtra("reminder_id", System.currentTimeMillis()).toInt()
            val title = intent.getStringExtra("reminder_title") ?: "تذكير موعد عمل"
            val message = intent.getStringExtra("reminder_message") ?: "لديك موعد أو مهمة عمل مستحقة الآن!"

            BackgroundReminderManager.sendNotification(
                context = context,
                notificationId = reminderId,
                title = title,
                message = message
            )
        } else if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Restore background reminders when the phone restarts
            val app = context.applicationContext as? BusinessApplication ?: return
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val appointments = app.repository.getAllAppointmentsOnce()
                    val now = System.currentTimeMillis()
                    appointments.forEach { appt ->
                        if (appt.dateTime > now) {
                            BackgroundReminderManager.scheduleReminder(
                                context = context,
                                reminderId = appt.id,
                                title = "تذكير بموعد: ${appt.title}",
                                message = "مع العميل: ${appt.clientName} في ${appt.locationOrLink}",
                                triggerAtMillis = appt.dateTime
                            )
                        }
                    }
                } catch (_: Exception) {
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
