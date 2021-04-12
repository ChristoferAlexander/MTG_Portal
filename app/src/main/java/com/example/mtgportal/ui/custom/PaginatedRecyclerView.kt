package com.example.mtgportal.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginatedRecyclerView : RecyclerView {

    var onBottomReachedListener: OnBottomReachedListener? = null

    var bottomReachOffset = 0

    private var _loading = false

    private var previousItemCount = 10

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        val linearLayoutManager = this.layoutManager as LinearLayoutManager?

        val itemCount = linearLayoutManager!!.itemCount

        if(previousItemCount != itemCount)
            _loading = false

        if (onBottomReachedListener != null
            && !_loading
            && itemCount > 0
            && previousItemCount != itemCount
            && itemCount <= linearLayoutManager.findLastVisibleItemPosition() + bottomReachOffset + 1
        ) {
            previousItemCount = linearLayoutManager.itemCount
            _loading = true
            onBottomReachedListener!!.onBottomReached()
        }
    }

    interface OnBottomReachedListener {
        fun onBottomReached()
    }
}