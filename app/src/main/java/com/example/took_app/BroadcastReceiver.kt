package com.example.took_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            action = "com.example.took_app.CHECK_AUTO_LOGIN"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(mainActivityIntent)
    }
}