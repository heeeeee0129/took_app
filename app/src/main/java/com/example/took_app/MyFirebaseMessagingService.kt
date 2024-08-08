package com.example.took_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.took_app.network.FCMApiService
import com.example.took_app.network.FCMTokenRequest
import com.example.took_app.network.RetrofitClient
import com.example.took_app.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "FCM 토큰: $token")
        Log.d("FCM", "onNewToken 호출됨")
        // 여기서 서버로 토큰을 전송하는 작업을 수행합니다.
        val userSeq = 4L // userSeq 가져오기
        val apiService = RetrofitClient.instance.create(FCMApiService::class.java)
        val request = FCMTokenRequest(userSeq, token) // 서버에 저장
        Log.d("request", "request: $request")
        apiService.saveToken(request).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("FCM", "토큰 저장 성공: ${response.body()}")
                } else {
                    Log.e("FCM", "토큰 저장 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("FCM", "토큰 저장 실패: ${t.message}")
            }
        })
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "메시지 받음: ${remoteMessage.notification?.body}")

        // 알림을 생성하여 사용자에게 표시하는 로직을 추가합니다.
        if (remoteMessage.notification != null) {
            sendNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        } else if (remoteMessage.data.isNotEmpty()) {
            // 데이터 메시지인 경우에도 알림을 생성합니다.
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            sendNotification(title, body)
        }
    }

    private fun sendNotification(title: String?, body: String?) {
        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        // 알림 채널 생성 (Android 8.0 이상)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림을 클릭했을 때 열릴 액티비티 설정
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w("FCM", "알림 권한이 없습니다.")
            return
        }

        // 알림 빌더 설정
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // 알림 아이콘 설정
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 알림 표시
        with(NotificationManagerCompat.from(this)) {
            notify(0, notificationBuilder.build())
        }
    }
}