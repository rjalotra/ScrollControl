package com.scrollcontrol.app.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusDmScreen(modifier: Modifier = Modifier) {
    var webView: WebView? by remember { mutableStateOf(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Intercept hardware/gesture back press to navigate inside webview
    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Focus DMs",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                if (canGoBack) {
                    IconButton(onClick = { webView?.goBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = {
                IconButton(onClick = { webView?.reload() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        userAgentString = settings.userAgentString.replace("; wv", "")
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                            canGoBack = view?.canGoBack() == true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            canGoBack = view?.canGoBack() == true

                            // Inject CSS to completely hide Reels and Explore navigation elements
                            val hideDistractionsCss = """
                                var style = document.createElement('style');
                                style.innerHTML = `
                                    a[href*="/reels/"],
                                    a[href*="/explore/"],
                                    svg[aria-label="Reels"],
                                    svg[aria-label="Explore"],
                                    [aria-label="Reels"],
                                    [aria-label="Explore"] {
                                        display: none !important;
                                    }
                                `;
                                document.head.appendChild(style);
                            """.trimIndent()
                            view?.evaluateJavascript(hideDistractionsCss, null)
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val targetUrl = request?.url?.toString() ?: ""
                            // Prevent navigating into the general reels feed
                            if (targetUrl.contains("instagram.com/reels/") && !targetUrl.contains("/reel/")) {
                                view?.loadUrl("https://www.instagram.com/direct/inbox/")
                                return true
                            }
                            return false
                        }
                    }

                    loadUrl("https://www.instagram.com/direct/inbox/")
                    webView = this
                }
            },
            update = {
                webView = it
            }
        )
    }
}
