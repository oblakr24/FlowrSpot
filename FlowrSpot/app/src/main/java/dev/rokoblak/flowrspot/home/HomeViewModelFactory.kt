package dev.rokoblak.flowrspot.home

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.paging.PagedList


class HomeViewModelFactory(private val dataSource: FlowersDataSource, private val config: PagedList.Config) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> {
                HomeViewModel(config, dataSource) as T
            }
            else -> TODO("No factory method provided for ${modelClass.canonicalName}")
        }
    }
}