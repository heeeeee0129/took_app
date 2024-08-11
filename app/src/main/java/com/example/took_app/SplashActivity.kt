package com.example.took_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val userDataStore by lazy { UserDataStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.IO).launch {
            val accessToken = userDataStore.getAccessToken()
            val refreshToken = userDataStore.getRefreshToken()
            val flag = if (accessToken != null && refreshToken != null) 1 else 0

            delay(10000)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            intent.putExtra("isLogin", flag)
            startActivity(intent)
        }
    }
}
