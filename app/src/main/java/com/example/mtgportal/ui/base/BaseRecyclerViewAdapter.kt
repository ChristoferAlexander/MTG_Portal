package com.example.mtgportal.ui.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<D, V : RecyclerView.ViewHolder> : RecyclerView.Adapter<V>() {

    private var _items: MutableList<D> = mutableListOf()

    override fun getItemCount(): Int = _items.size

    fun getItems(): List<D> {
        return _items
    }

    fun setItems(list: List<D>) {
        this._items.clear()
        this._items.addAll(list)
        notifyDataSetChanged()
    }

    fun addItems(data: List<D>) {
        val index = this._items.size
        this._items.addAll(data)
        notifyItemRangeInserted(index, data.size)
    }

    fun clearItems() {
        _items.clear()
        notifyDataSetChanged()
    }

    fun removeItem(item: D) {
        val itemIndex = _items.indexOf(item)
        if (itemIndex != -1) {
            _items.remove(item)
            notifyItemRemoved(itemIndex)
        }
    }
}