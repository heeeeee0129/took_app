package com.example.took_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private val dataStore = UserDataStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.IO).launch {

            val accessToken = dataStore.getAccessToken()
            val refreshToken = dataStore.getRefreshToken()
            var flag = -1

            if(accessToken != null && refreshToken != null) flag = 1
            else flag = 0

            delay(5000)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            intent.putExtra("isLogin",flag)
            startActivity(intent)
        }

    }

}