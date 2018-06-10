package dev.rokoblak.flowrspot.utils

import android.view.View
import android.widget.TextView

/**
 * A base loading/content/empty view interface with some transition animations
 */
interface ContentLoadingView {

    val contentView: View
    val loadingView: View
    val emptyView: View
    val emptyMessage: TextView

    private fun getPreviouslyVisible(): View {
        return when {
            emptyView.visibility == View.VISIBLE -> emptyView
            loadingView.visibility == View.VISIBLE -> loadingView
            else -> contentView
        }
    }

    fun showLoading() {
        contentView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    fun showContent(animate: Boolean = true) {
        if (animate) {
            contentView.crossFadeFrom(getPreviouslyVisible(), 400L)
        } else {
            contentView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            loadingView.visibility = View.GONE
        }
    }

    fun showEmpty(message: String = "No data", animate: Boolean = true) {
        if (animate) {
            emptyView.crossFadeFrom(getPreviouslyVisible(), 400L)
        } else {
            contentView.visibility = View.GONE
            loadingView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        }
        emptyMessage.text = message
    }
}