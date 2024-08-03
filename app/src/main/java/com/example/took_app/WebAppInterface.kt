package com.example.took_app

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager


class WebAppInterface(private val context: Context, private val webView: WebView ) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
            webView.evaluateJavascript("javascript:receiveDataFromAndroid('$data')", null)
        }
    }

    @SuppressLint("MissingPermission")
    @JavascriptInterface
    fun getUserLocation() {
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    webView.post {
                        webView.evaluateJavascript("javascript:receiveLocationFromAndroid(${location.latitude}, ${location.longitude})", null)
                    }
                    locationManager.removeUpdates(this)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            })
        } else {
            Toast.makeText(context, "Location permissions are not granted", Toast.LENGTH_SHORT).show()
        }
    }

}