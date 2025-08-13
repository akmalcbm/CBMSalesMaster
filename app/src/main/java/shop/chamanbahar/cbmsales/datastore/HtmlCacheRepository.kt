package shop.chamanbahar.cbmsales.datastore

import android.content.Context
import kotlinx.coroutines.flow.first

class HtmlCacheRepository(private val context: Context) {

    private val dataStore = context.htmlCacheDataStore

    suspend fun getHtml(productId: String): String? {
        val entry = dataStore.data.first()
        return entry.entriesMap[productId]
    }

    suspend fun cacheHtml(productId: String, html: String) {
        dataStore.updateData { current ->
            current.toBuilder()
                .putEntries(productId, html)
                .build()
        }
    }
}
