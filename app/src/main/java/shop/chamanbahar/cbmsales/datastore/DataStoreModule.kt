package shop.chamanbahar.cbmsales.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore

val Context.htmlCacheDataStore: DataStore<HtmlCacheEntry> by dataStore(
    fileName = "html_cache.pb",
    serializer = HtmlCacheEntrySerializer
)