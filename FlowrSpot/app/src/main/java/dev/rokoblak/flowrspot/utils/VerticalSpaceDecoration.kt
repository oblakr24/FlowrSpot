package dev.rokoblak.flowrspot.utils

import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class VerticalSpaceDecoration(private val verticalSpaceHeight: Int, private val lastItemMargin: Int = -1) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State?) {

        val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
        val itemCount = parent.adapter.itemCount
        val mod = itemCount % spanCount
        val lastRowFirstIdx = if (mod == 0) itemCount - spanCount else itemCount - mod
        val adapterPosition = parent.getChildAdapterPosition(view)
        if (adapterPosition < lastRowFirstIdx) {
            outRect.bottom = verticalSpaceHeight
        } else if (lastItemMargin >= 0) {
            outRect.bottom = lastItemMargin
        }
    }
}