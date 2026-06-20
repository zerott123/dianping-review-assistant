package com.dianping.assistant

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.dianping.assistant.api.DeepSeekApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val api = DeepSeekApi(BuildConfig.DEEPSEEK_API_KEY)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            setBackgroundColor(0x00000000)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val apiKey = Json.encodeToString(BuildConfig.DEEPSEEK_API_KEY)
                    view?.evaluateJavascript("window.API_KEY = $apiKey;", null)
                }
                override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                    Log.e("WebView", "Error $errorCode: $description @ $failingUrl")
                }
            }
            addJavascriptInterface(JsBridge(), "Android")
            loadUrl("file:///android_asset/index.html")
        }
        setContentView(webView)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    inner class JsBridge {
        @JavascriptInterface
        fun generate(input: String, sentiment: String, style: String, wordCount: String) {
            scope.launch {
                val systemPrompt = PromptBuilder.systemPrompt()
                val userPrompt = PromptBuilder.userPrompt(input, sentiment, style, wordCount)
                api.generateReview(systemPrompt, userPrompt)
                    .onSuccess { result ->
                        webView.evaluateJavascript(
                            "window.onResult(${Json.encodeToString(result)})",
                            null
                        )
                    }
                    .onFailure { e ->
                        val msg = e.message ?: "生成失败，请重试"
                        webView.evaluateJavascript(
                            "window.onError(${Json.encodeToString(msg)})",
                            null
                        )
                    }
            }
        }

        @JavascriptInterface
        fun copy(text: String) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("评价", text)
            clipboard.setPrimaryClip(clip)
        }
    }
}
