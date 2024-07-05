package com.familylocation.mobiletracker.callThemesModule.supportFunctions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Int, private val spanCount: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        outRect.left = space - column * space / spanCount // spacing - column * ((1f / spanCount) * spacing)
        outRect.right = (column + 1) * space / spanCount // (column + 1) * ((1f / spanCount) * spacing)

        if (position < spanCount) { // top edge
            outRect.top = space
        }
        outRect.bottom = space // item bottom
    }
}