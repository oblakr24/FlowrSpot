package dev.rokoblak.flowrspot.network

import dev.rokoblak.flowrspot.data.FlowersResponse
import kotlinx.coroutines.experimental.Deferred

/**
 * A wrapper around the API service
 */
interface FlowersServiceWrapper {

    fun getFlowers(filterParam: String, pageIdx: Int, count: Int): Deferred<FlowersResponse>

}