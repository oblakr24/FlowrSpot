package dev.rokoblak.flowrspot.network

import dev.rokoblak.flowrspot.data.FlowersResult
import dev.rokoblak.flowrspot.data.NetworkResult
import java.io.IOException
import java.net.SocketTimeoutException


open class FlowersRepository(private val serviceWrapper: FlowersServiceWrapper) {

    private var maxPageIdx = 1

    var query: String = ""

    // Loads a page with a given item count
    open suspend fun loadPage(pageIdx: Int, count: Int): FlowersResult {
        return try {
            var nextPageIdx: Int? = null
            val flowersList = when {
                pageIdx <= maxPageIdx -> {
                    val result = serviceWrapper.getFlowers(query, pageIdx, count).await()
                    val pagination = result.meta.pagination
                    maxPageIdx = pagination.total_pages ?: 1
                    nextPageIdx = pagination.next_page
                    result.flowers
                }
                else -> {
                    // Max page index reached,
                    // return an empty list to let the database know there's no more data
                    emptyList()
                }
            }

            // Notify about no data only if this is the first page being loaded
            if (flowersList.isEmpty() && pageIdx == 0) {
                FlowersResult.Failure(NetworkResult.NoData)
            } else {
                FlowersResult.Success(flowersList, nextPageIdx)
            }

        } catch (e: SocketTimeoutException) {
            // Timeout
            e.printStackTrace()
            FlowersResult.Failure(NetworkResult.Timeout)
        } catch (e: IOException) {
            // Network problem
            e.printStackTrace()
            FlowersResult.Failure(NetworkResult.NoNetwork)
        } catch (e: Exception) {
            // Other error
            e.printStackTrace()
            FlowersResult.Failure(NetworkResult.Error(e))
        }
    }
}