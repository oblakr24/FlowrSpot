package dev.rokoblak.flowrspot.network

import dev.rokoblak.flowrspot.data.FlowersResponse
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface FlowersAPIService {

    @GET("flowers/search")
    fun getFlowers(@Query("query") filterParam: String,
                   @Query("page") page: Int
    ): Deferred<FlowersResponse>
}