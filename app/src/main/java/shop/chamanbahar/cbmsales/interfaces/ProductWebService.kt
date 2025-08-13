package shop.chamanbahar.cbmsales.interfaces

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ProductWebService {
    @GET
    suspend fun getDescription(@Url url: String): Response<ResponseBody>
}
