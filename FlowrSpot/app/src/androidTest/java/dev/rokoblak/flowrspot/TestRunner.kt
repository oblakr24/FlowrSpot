package dev.rokoblak.flowrspot

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import dev.rokoblak.flowrspot.app.FlowrApp
import dev.rokoblak.flowrspot.data.FlowersResponse
import dev.rokoblak.flowrspot.data.Meta
import dev.rokoblak.flowrspot.data.Pagination
import dev.rokoblak.flowrspot.network.DataProvider
import dev.rokoblak.flowrspot.network.FlowersAPIService
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Used to set a custom data provider to the Application class
 */
class TestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {

        val app = super.newApplication(cl, FlowrApp::class.java.name, context) as FlowrApp

        val allResponse = TestHelper.allItemsResponse!!

        // Set a fake data provider
        app.customDataProvider = object : DataProvider {

            // Doesn't matter, as long as it's a valid URL
            override fun provideBaseUrl() = "http://flowrspot-api.herokuapp.com/api/v1/"

            // Fake tracker service
            override fun provideTrackerService(): FlowersAPIService {
                return object : FlowersAPIService {
                    override fun getFlowers(filterParam: String, page: Int): Deferred<FlowersResponse> {
                        if (page > 1) {
                            return async {
                                FlowersResponse(
                                        emptyList(),
                                        Meta(Pagination(0, null, null, 1))
                                )
                            }
                        }
                        // Do the filtering manually
                        return async { allResponse.filterItems(filterParam) }
                    }
                }
            }
        }

        return app

    }
}