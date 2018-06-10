package dev.rokoblak.flowrspot.network

import dev.rokoblak.flowrspot.data.FlowersResponse
import dev.rokoblak.flowrspot.data.Meta
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * A custom wrapper around a service which simulates some more pages with fake data
 */
class DuplicationFlowersServiceWrapper(private val service: FlowersAPIService) : FlowersServiceWrapper {

    // The API starts counting pages from 1
    private val startPageIdx = 1

    // Add some additional fake pages
    private val additionalPages = 9

    override fun getFlowers(filterParam: String, pageIdx: Int, count: Int): Deferred<FlowersResponse> {
        // The API doesn't seem to accept any parameters controlling the item count,
        // so this param is ignored
        return async {
            val result = service.getFlowers(filterParam, startPageIdx).await()

            val flowers = if (pageIdx > startPageIdx) {
                result.flowers.map {
                    it.copy(name = "(${pageIdx - startPageIdx}) ${it.name}")
                }
            } else {
                result.flowers
            }
            val paginationInfo = result.meta.pagination
            val fakeTotalPages = paginationInfo.total_pages?.let {
                it + additionalPages
            } ?: additionalPages + 1
            val pagination = paginationInfo.copy(total_pages = fakeTotalPages)
            val newMeta = Meta(pagination)
            FlowersResponse(flowers, newMeta)
        }
    }
}