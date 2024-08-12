package com.example.took_app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WebAppInterface(private val context: Context, private val webView: WebView) {

    private lateinit var locationManager: LocationManager
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var locationRunnable: Runnable
    private val userDataStore = UserDataStore(context)  // 수정된 부분

    @JavascriptInterface
    fun showToast(toast: String) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun getData(): String {
        return "Hello from Android"
    }

    @JavascriptInterface
    fun performAction(data: String) {
        webView.post {
            webView.evaluateJavascript("javascript:receiveData('$data')", null)
        }
    }

    @JavascriptInterface
    fun getLocation() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, locationListener)

            locationRunnable = Runnable {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { location ->
                    sendLocationToWeb(location.latitude, location.longitude)
                }
                handler.postDelayed(locationRunnable, 30000)
            }
            handler.post(locationRunnable)

        } else {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendLocationToWeb(latitude: Double, longitude: Double) {
        webView.post {
            webView.evaluateJavascript("javascript:onLocation($latitude, $longitude)", null)
        }
    }

    @JavascriptInterface
    fun sendNotificationToWeb(notificationData: String) {
        webView.post {
            webView.evaluateJavascript("javascript:onNotification($notificationData)", null)
        }
    }

    @JavascriptInterface
    fun authenticate() {
        val mainActivity = MainActivity.getInstance()
        mainActivity?.startBiometricAuthentication()
    }

    @JavascriptInterface
    fun getTokenFromWeb(id: String, pwd: String, userSeq: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val userSeqLong: Long = userSeq.toLong()
            userDataStore.saveUserId(id)
            userDataStore.saveUserPassword(pwd)
            userDataStore.saveUserSeq(userSeqLong) // userSeq 저장
            userDataStore.saveIsLoggedIn(true)

        }
    }

    @JavascriptInterface
    fun goTookChat() {
        webView.post {
            webView.evaluateJavascript("javascript:onAlarm()", null)
        }
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

//    fun getTokenFromWeb() {
//        webView.evaluateJavascript("javascript:postToken()", { token ->
//            token?.let {
//                CoroutineScope(Dispatchers.IO).launch {
//                    userDataStore.saveRefreshToken(it)
//                }
//            }
//        })
//    }
}
