package shop.chamanbahar.cbmsales.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import shop.chamanbahar.cbmsales.interfaces.ProductWebService

object WebServiceBuilder {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://chamanbahar.shop/") // Dummy base, real URLs will be overridden
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val api: ProductWebService = retrofit.create(ProductWebService::class.java)
}

object HtmlCache {
    private val htmlMap = mutableMapOf<String, String>()

    fun get(url: String): String? = htmlMap[url]

    fun put(url: String, html: String) {
        htmlMap[url] = html
    }

    fun clear() {
        htmlMap.clear()
    }
}