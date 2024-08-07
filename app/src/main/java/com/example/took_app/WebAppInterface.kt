package com.example.took_app

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity


class WebAppInterface(private val context: Context, private val webView: WebView ) {

    private lateinit var locationManager: LocationManager
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var locationRunnable: Runnable

    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun getData(): String {
        // 여기에 원하는 데이터를 반환하는 로직을 추가합니다.
        return "Hello from Android"
    }

    @JavascriptInterface
    fun performAction(data: String) {
        // 데이터에 따라 앱에서 작업을 수행합니다.
        // 작업 완료 후 결과를 웹뷰로 다시 전달할 수 있습니다.
        webView.post {
            webView.evaluateJavascript("javascript:receiveData('$data')", null)
        }
    }


    @JavascriptInterface
    fun getLocation() {
        // LocationManager 초기화
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 위치 권한 확인
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 위치 업데이트 요청
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, locationListener)

            // 30초 주기로 위치 정보를 자바스크립트로 전송하는 타이머 설정
            locationRunnable = Runnable {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { location ->
                    sendLocationToWeb(location.latitude, location.longitude)
                }
                handler.postDelayed(locationRunnable, 30000) // 30초 후 다시 실행
            }
            handler.post(locationRunnable)

        } else {
            // 권한이 없을 경우
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendLocationToWeb(latitude: Double, longitude: Double) {
        webView.post {
            webView.evaluateJavascript("javascript:onLocation($latitude, $longitude)", null)
        }
    }

    @JavascriptInterface
    fun sendNotificationToWeb(notificationData: String){
        webView.post{
            webView.evaluateJavascript("javascript:onNotification($notificationData)", null)
        }
    }

    @JavascriptInterface
    fun authenticate() {
//        Toast.makeText(context, "생체인증", Toast.LENGTH_SHORT).show()
        val mainActivity = MainActivity.getInstance()
        mainActivity?.startBiometricAuthentication()

    }
    fun sendAuthenticationResultToWeb(success: Boolean) {
        webView.post {
            webView.evaluateJavascript("javascript:onAuthenticate($success)", null)
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            // JavaScript로 위치 값을 전달
            webView.post {
                webView.evaluateJavascript("javascript:onLocation($latitude, $longitude)", null)
            }

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun onBiometricAuthenticationSuccess() {
        webView.post {
            webView.evaluateJavascript("javascript:onBiometricAuthenticationSuccess()", null)
        }
    }

}