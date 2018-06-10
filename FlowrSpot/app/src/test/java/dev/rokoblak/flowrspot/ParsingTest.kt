package dev.rokoblak.flowrspot

import dev.rokoblak.flowrspot.data.Flower
import dev.rokoblak.flowrspot.data.FlowersResponse
import dev.rokoblak.flowrspot.data.FlowersResult
import dev.rokoblak.flowrspot.data.Pagination
import dev.rokoblak.flowrspot.network.DataProvider
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert
import org.junit.Test

/**
 * Tests JSON parsing
 */
class ParsingTest {

    // The response from the API's first page, read to a string
    private val allItemsResponse by lazy {
        TestHelper.allItemsStringResponse
    }

    private fun checkResponse(response: FlowersResponse) {

        val pagination = response.meta.pagination

        /**
        "current_page": 1,
        "prev_page": null,
        "next_page": null,
        "total_pages": 1
         */

        val expectedPagination = Pagination(
                1,
                null,
                null,
                1
        )

        Assert.assertEquals(pagination, expectedPagination)

        checkItems(response.flowers)
    }

    private fun checkItems(items: List<Flower>) {
        Assert.assertEquals(items.size, 22)

        val testFlower = Flower(
                123,
                "TestName",
                "TestNameLatin",
                321,
                "testImageUrl",
                true
        )

        Assert.assertEquals(items.first(), testFlower)

        /**
        "id": 2,
        "name": "Lily",
        "latin_name": "Liliy in latin",
        "sightings": 0,
        "profile_picture": "//flowrspot.s3.amazonaws.com/flowers/profile_pictures/000/000/002/medium/dahlia-1627138_960_720.jpg?1519073382",
        "favorite": false
         */

        val flowerOne = Flower(
                2,
                "Lily",
                "Liliy in latin",
                0,
                "//flowrspot.s3.amazonaws.com/flowers/profile_pictures/000/000/002/medium/dahlia-1627138_960_720.jpg?1519073382",
                false
        )

        Assert.assertEquals(items[1], flowerOne)

        /**
        "id": 21,
        "name": "Ljubicica",
        "latin_name": "Ljbicica latinski",
        "sightings": 0,
        "profile_picture": "//flowrspot.s3.amazonaws.com/flowers/profile_pictures/000/000/021/medium/images.jpg?1528071541",
        "favorite": false
         */

        val lastFlower = Flower(
                21,
                "Ljubicica",
                "Ljbicica latinski",
                0,
                "//flowrspot.s3.amazonaws.com/flowers/profile_pictures/000/000/021/medium/images.jpg?1528071541",
                false
        )

        Assert.assertEquals(items.last(), lastFlower)
    }

    /**
     * Mocked data provider
     */
    private val dataProvider: DataProvider by lazy { TestHelper.dataProvider }

    private val moshi by lazy { dataProvider.provideMoshi() }

    private val service by lazy { dataProvider.provideTrackerService() }

    private val repository by lazy { dataProvider.provideRepository() }

    /**
     * Test whether Moshi correctly maps the JSON into the target class
     */
    @Test
    fun testMoshiParsing() {
        val jsonAdapter = moshi.adapter(FlowersResponse::class.java)
        val response = jsonAdapter.fromJson(allItemsResponse)

        Assert.assertNotNull(response)
        checkResponse(response!!)
    }

    /**
     * Test whether the API service parses the data correctly
     */
    @Test
    fun testServiceParsing() = runBlocking {
        val response = service.getFlowers("", 1).await()
        checkResponse(response)
    }

    /**
     * Tests whether the repository returns te correct data
     */
    @Test
    fun testRepositoryParsing() = runBlocking {

        val result = repository.loadPage(0, 20)

        Assert.assertTrue(result is FlowersResult.Success)

        val successResult = result as FlowersResult.Success

        checkItems(successResult.data)

        Assert.assertEquals(successResult.nextPageIndex, null)
    }
}