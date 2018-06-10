package dev.rokoblak.flowrspot

import android.arch.paging.PagedList
import android.support.test.runner.AndroidJUnit4
import dev.rokoblak.flowrspot.home.FlowersDataSource
import dev.rokoblak.flowrspot.home.HomeViewModel
import kotlinx.coroutines.experimental.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ViewModelTest {

    private val dataProvider by lazy { TestHelper.dataProvider }

    /**
     * Subscribes to the view model's paged list live data
     */
    @Test
    fun testPagedListLiveData() {

        val config =
                PagedList.Config.Builder()
                        .setPageSize(20)
                        .setInitialLoadSizeHint(20)
                        .build()

        val repository = dataProvider.provideRepository()
        val dataSource = FlowersDataSource(repository)
        val viewModel = HomeViewModel(config, dataSource)

        launch {
            val pagedListLiveData = viewModel.getFlowers("")
            pagedListLiveData.observeForever { pagedList ->

                // Must not be null
                Assert.assertNotNull(pagedList)
                /**
                 * Here the items count is (correctly) 0,
                 * the paged list updates the adapter separately
                 * Unfortunately paged list doesn't support any callbacks
                 * to inform us when a new page is loaded
                 */
            }

            Thread.sleep(2000)
        }
    }
}
