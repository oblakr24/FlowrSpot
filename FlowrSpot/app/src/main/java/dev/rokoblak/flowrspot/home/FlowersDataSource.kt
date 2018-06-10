package dev.rokoblak.flowrspot.home

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import dev.rokoblak.flowrspot.data.Flower
import dev.rokoblak.flowrspot.data.FlowersResult
import dev.rokoblak.flowrspot.data.NetworkResult
import dev.rokoblak.flowrspot.network.FlowersRepository
import kotlinx.coroutines.experimental.launch


/**
 * Handles the page loading for use in the paged list
 */
class FlowersDataSource(private val repository: FlowersRepository)
    : PageKeyedDataSource<Int, Flower>() {

    private var resultLiveData: MutableLiveData<NetworkResult>? = null

    fun setQuery(query: String) {
        repository.query = query
    }

    fun setNetworkResultLiveData(networkResultLiveData: MutableLiveData<NetworkResult>) {
        resultLiveData = networkResultLiveData
    }

    // Load the first page
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Flower>) {
        launch {
            val (flowers, nextPageIdx) = repository.loadPage(0, params.requestedLoadSize)
                    .handle(resultLiveData)
            callback.onResult(flowers, null, nextPageIdx ?: 1)
        }
    }

    // Load any next page
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Flower>) {
        launch {
            val (flowers, nextPageIdx) = repository.loadPage(params.key, params.requestedLoadSize)
                    .handle(resultLiveData)
            callback.onResult(flowers, nextPageIdx ?: params.key+1)
        }
    }

    // Do nothing - we're only appending, previously loaded data doesn't need updating
    // (without the user explicitly wanting so via the swipe to refresh widget)
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Flower>) {}

    private fun FlowersResult.handle(networkResultLiveData: MutableLiveData<NetworkResult>?)
            : Pair<List<Flower>, Int?> = when (this) {
        is FlowersResult.Success -> {
            networkResultLiveData?.postValue(NetworkResult.Success)
            data to nextPageIndex
        }
        is FlowersResult.Failure -> {
            networkResultLiveData?.postValue(networkResult)
            emptyList<Flower>() to null
        }
    }
}