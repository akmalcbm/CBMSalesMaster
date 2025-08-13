package shop.chamanbahar.cbmsales.screens

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shop.chamanbahar.cbmsales.datastore.HtmlCacheRepository
import shop.chamanbahar.cbmsales.network.WebServiceBuilder

@Composable
fun DescriptionTabContent(url: String, productId: String) {
    var htmlContent by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    if (url.isBlank()) {
        Text(
            text = "No product description available.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    LaunchedEffect(url, productId) {
        isLoading = true
        htmlContent = fetchHtml(url, productId, context)
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        htmlContent?.let {
            WebViewWrapper(it)
        } ?: Text(
            text = "Description not available.",
            modifier = Modifier.padding(16.dp)
        )
    }
}


suspend fun fetchHtml(url: String, productId: String, context: Context): String? {
    val cacheRepo = HtmlCacheRepository(context)

    // 1️⃣ Try from cache first
    cacheRepo.getHtml(productId)?.let { return it }

    // 2️⃣ If not found, fetch from server
    return withContext(Dispatchers.IO) {
        try {
            val response = WebServiceBuilder.api.getDescription(url)
            if (response.isSuccessful) {
                val html = response.body()?.string()
                if (html != null) {
                    cacheRepo.cacheHtml(productId, html) // 💾 Save to DataStore
                }
                html
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewWrapper(htmlContent: String) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}