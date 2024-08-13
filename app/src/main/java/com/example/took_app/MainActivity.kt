package com.example.took_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var myWebView: WebView
    private lateinit var webAppInterface: WebAppInterface
    private val userDataStore by lazy { UserDataStore() }

    init {
        instance = this
    }

    companion object {
        private var instance: MainActivity? = null

        fun getInstance(): MainActivity? {
            return instance
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Permissions granted
        } else {
            // Permissions not granted
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isLogin = intent.getIntExtra("isLogin", -1)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("FCM", "알림 권한이 이미 허용되었습니다.")
            } else {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }

        myWebView = findViewById(R.id.webview)
        myWebView.settings.javaScriptEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)
        myWebView.settings.domStorageEnabled = true

        webAppInterface = WebAppInterface(this, myWebView)
        myWebView.addJavascriptInterface(webAppInterface, "Android")
        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                checkAutoLogin()
            }
        }
        myWebView.loadUrl("https://i11e205.p.ssafy.io")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun checkAutoLogin() {
        CoroutineScope(Dispatchers.IO).launch {
            val token = userDataStore.getToken()
            if (token != null) {
                val seq = userDataStore.getUserSeq()
                if (seq != null) {
                    myWebView.post {
                        myWebView.evaluateJavascript("javascript:onLogin('$seq', '$token')", null)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    fun startBiometricAuthentication() {
        CoroutineScope(Dispatchers.Main).launch {
            val biometricManager = BiometricManager.from(this@MainActivity)
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) != BiometricManager.BIOMETRIC_SUCCESS) {
                webAppInterface.sendAuthenticationResultToWeb(false)
                return@launch
            }

            val executor = ContextCompat.getMainExecutor(this@MainActivity)
            val biometricPrompt = BiometricPrompt(this@MainActivity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d("biometric test", "인증 에러")
                    webAppInterface.sendAuthenticationResultToWeb(false)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d("biometric test", "인증 실패")
                    webAppInterface.sendAuthenticationResultToWeb(false)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d("biometric test", "인증 성공")
                    webAppInterface.sendAuthenticationResultToWeb(true)
                }
            })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }

    override fun onBackPressed() { // 웹뷰 뒤로가기 처리
        if(myWebView.canGoBack()) myWebView.goBack()
        else super.onBackPressed()
    }
}
