package com.example.mtgportal.ui.custom

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SearchView
import com.example.mtgportal.R

class CustomSearchView : SearchView {

    var listener: OnQueryTextListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        super.setOnQueryTextListener(listener)
        this.listener = listener
        val mSearchSrcTextView: SearchAutoComplete = this.findViewById(R.id.search_src_text)
        mSearchSrcTextView.setOnEditorActionListener { _, _, _ ->
            listener?.onQueryTextSubmit(query.toString())
            true
        }
    }
}