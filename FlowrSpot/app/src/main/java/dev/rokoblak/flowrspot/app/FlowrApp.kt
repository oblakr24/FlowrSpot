package dev.rokoblak.flowrspot.app

import android.app.Application
import dev.rokoblak.flowrspot.BuildConfig
import dev.rokoblak.flowrspot.network.DataProvider
import dev.rokoblak.flowrspot.network.DuplicationFlowersServiceWrapper
import dev.rokoblak.flowrspot.network.FlowersServiceWrapper


class FlowrApp : Application() {

    private val baseDataProvider: DataProvider by lazy {
        object : DataProvider {

            override fun provideBaseUrl() = "http://flowrspot-api.herokuapp.com/api/v1/"

            override fun provideServiceWrapper(): FlowersServiceWrapper {
                return if (BuildConfig.BUILD_TYPE == "debug") {
                    // This wrapper fakes any pages other than the first one to fake pagination
                    DuplicationFlowersServiceWrapper(provideTrackerService())
                } else {
                    super.provideServiceWrapper()
                }
            }
        }
    }

    // Can be set externally by tests
    var customDataProvider: DataProvider? = null

    val dataSource by lazy {
        val dataProvider = customDataProvider ?: baseDataProvider
        dataProvider.provideDataSource()
    }
}