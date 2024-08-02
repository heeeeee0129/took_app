package com.example.took_app

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast


class WebAppInterface(private val context: Context, private val webView: WebView ) {

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

}