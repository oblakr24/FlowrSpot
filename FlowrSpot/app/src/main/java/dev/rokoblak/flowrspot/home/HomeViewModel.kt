package dev.rokoblak.flowrspot.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import dev.rokoblak.flowrspot.data.Flower
import dev.rokoblak.flowrspot.data.NetworkResult

/**
 * Provides two live data subscribables:
 * 1. dataResult, informing the subscriber about the current data state
 * 2. a pagedList of flowers
 */
class HomeViewModel(
        private val config: PagedList.Config,
        private val dataSource: FlowersDataSource
) : ViewModel() {

    private val networkResult: MutableLiveData<NetworkResult> by lazy {
        MutableLiveData<NetworkResult>()
    }

    /**
     * Returns a live data notifying about the current data state
     */
    fun getDataResult(): LiveData<NetworkResult> = networkResult

    /**
     * Returns a new paged list live data
     */
    fun getFlowers(filter: String): LiveData<PagedList<Flower>> {
        return LivePagedListBuilder<Int, Flower>(
                object : DataSource.Factory<Int, Flower>() {
                    override fun create(): DataSource<Int, Flower> {
                        dataSource.setQuery(filter)
                        dataSource.setNetworkResultLiveData(networkResult)
                        return dataSource
                    }

                }, config)
                .build()
    }
}