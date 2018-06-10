package dev.rokoblak.flowrspot

import android.os.SystemClock
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import dev.rokoblak.flowrspot.home.HomeActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.replaceText
import dev.rokoblak.flowrspot.EspressoUtils.withCount


@RunWith(AndroidJUnit4::class)
@LargeTest
class CheckFilteringTest {

    @Rule
    @JvmField
    val mActivityRule = ActivityTestRule(HomeActivity::class.java)

    /**
     * Input different query combinations and check if the correct
     * number of items are displayed in the RecyclerView
     */
    @Test
    fun checkFiltering() {

        inputQueryAndCheck("")

        inputQueryAndCheck("lil")

        inputQueryAndCheck("pur")

        inputQueryAndCheck("asdf")
    }

    private fun inputQueryAndCheck(query: String) {
        onView(ViewMatchers.withId(R.id.edit_search)).perform(replaceText(query))
        val expectedResult = TestHelper.allItemsResponse.filterItems(query)
        SystemClock.sleep(600) // The request is executed 500 ms after the last char is entered, so we need to wait a second
        onView(ViewMatchers.withId(R.id.recycler_flowers)).check(withCount(expectedResult.flowers.size))
    }
}