package dev.rokoblak.flowrspot

import android.support.test.espresso.ViewAssertion
import android.support.v7.widget.RecyclerView
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert


object EspressoUtils {

    fun withCount(expectedCount: Int) = ViewAssertion { view, noViewFoundException ->
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        MatcherAssert.assertThat(adapter.itemCount, CoreMatchers.`is`(expectedCount))
    }

}