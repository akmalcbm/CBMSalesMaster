package shop.chamanbahar.cbmsales.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object HtmlCacheEntrySerializer : Serializer<HtmlCacheEntry> {
    override val defaultValue: HtmlCacheEntry = HtmlCacheEntry.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): HtmlCacheEntry {
        try {
            return HtmlCacheEntry.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: HtmlCacheEntry, output: OutputStream) {
        t.writeTo(output)
    }
}