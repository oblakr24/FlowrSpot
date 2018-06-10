package dev.rokoblak.flowrspot

import dev.rokoblak.flowrspot.data.FlowersResponse
import dev.rokoblak.flowrspot.network.DataProvider
import okhttp3.*


object TestHelper {

    // The response from the API's first page, read to a string
    val allItemsStringResponse by lazy {
        javaClass.classLoader.getResourceAsStream("response_all.json")
                .reader().readText()
    }

    // The parsed response from the API's first page
    val allItemsResponse by lazy {
        dataProvider.provideMoshi().adapter(FlowersResponse::class.java).fromJson(allItemsStringResponse)!!
    }

    // The data provider used in some tests
    val dataProvider: DataProvider by lazy {
        object : DataProvider {

            // Doesn't really matter for this test, as long as it's a valid URL
            override fun provideBaseUrl() = "http://flowrspot-api.herokuapp.com/api/v1/"

            // Always returns the all responses .json in this case
            override fun provideOKHttpInterceptor(): Interceptor? {
                return Interceptor { chain ->
                    Response.Builder()
                            .code(200)
                            .message(allItemsStringResponse)
                            .request(chain!!.request())
                            .protocol(Protocol.HTTP_1_0)
                            .body(ResponseBody.create(MediaType.parse("application/json"), allItemsStringResponse.toByteArray()))
                            .addHeader("content-type", "application/json")
                            .build()
                }
            }
        }
    }
}

/**
 * Manually filter the items by a query criterion (used in tests only)
 */
fun FlowersResponse.filterItems(filterParam: String): FlowersResponse {
    val query = filterParam.toLowerCase()
    return copy(flowers = if (query.isBlank()) {
        flowers
    } else {
        flowers.filter {
            it.name.toLowerCase().contains(query)
                    || it.latin_name.toLowerCase().contains(query)
        }
    })
}