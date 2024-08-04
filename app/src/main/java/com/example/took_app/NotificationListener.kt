package com.example.took_app

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener: NotificationListenerService() {
    private val TAG = "NotificationListenerService"
    private var webAppInterface: WebAppInterface? = null

    fun setWebAppInterface(webAppInterface: WebAppInterface){
        this.webAppInterface = webAppInterface
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName: String = sbn?.packageName ?: "Null"
        val extras = sbn?.notification?.extras

        val extraTitle: String = extras?.getString(Notification.EXTRA_TITLE) ?: "No Title"
        val extraText: String = extras?.getString(Notification.EXTRA_TEXT) ?: "No Text"
        val extraBigText: String = extras?.getString(Notification.EXTRA_BIG_TEXT) ?: "No BigText"
        val extraInfoText: String = extras?.getString(Notification.EXTRA_INFO_TEXT) ?: "No InfoText"
        val extraSubText: String = extras?.getString(Notification.EXTRA_SUB_TEXT) ?: "No SubText"
        val extraSummaryText: String = extras?.getString(Notification.EXTRA_SUMMARY_TEXT) ?: "No SummaryText"

        val notificationData = """
            {
                "packageName": "$packageName",
                "title": "$extraTitle",
                "text": "$extraText",
                "bigText": "$extraBigText",
                "infoText": "$extraInfoText",
                "subText": "$extraSubText",
                "summaryText": "$extraSummaryText"
            }
        """.trimIndent()

        Log.d(TAG, "onNotificationPosted: $notificationData")

        webAppInterface?.sendNotificationToWeb(notificationData)
    }

}