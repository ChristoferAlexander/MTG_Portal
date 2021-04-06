package com.example.mtgportal.ui.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<D,V: RecyclerView.ViewHolder>: RecyclerView.Adapter<V>() {

    private var _items: MutableList<D> = mutableListOf()

    override fun getItemCount(): Int = _items.size

    fun getItems(): List<D>{
        return _items
    }

    fun setItems(list: List<D>) {
        this._items.clear()
        this._items.addAll(list)
        notifyDataSetChanged()
    }

    fun updateItems(list: MutableList<D>) {
        this._items = list
        notifyDataSetChanged()
    }

    fun insertItem(data: D, position: Int? = null) {
        if (position != null) {
            this._items.add(position, data)
            notifyItemInserted(position)
        } else {
            this._items.add(data)
            notifyItemInserted(this._items.size - 1)
        }
    }

    fun updateItem(data: D, position: Int) {
        this._items[position] = data
        notifyItemChanged(position)
    }

    fun removeItem(index: Int) {
        if (index < _items.size) {
            _items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun insertItems(data: List<D>, position: Int? = null) {
        if (position != null) {
            this._items.addAll(position, data)
            notifyItemRangeInserted(position, data.size)
        } else {
            val index = this._items.size - 1
            this._items.addAll(data)
            notifyItemRangeInserted(index, this._items.size - 1)
        }
    }

    fun updateItems(data: List<D>, startIndex: Int) {
        var start = startIndex
        for (i in data.indices) {
            if (start >= this._items.size) {
                this._items.add(data[i])
            } else {
                this._items[start] = data[i]
            }
            start++
        }
        notifyItemRangeChanged(startIndex, data.size)
    }

    fun removeItems(startIndex: Int, endIndex: Int = this._items.size - 1) {
        val iterator = this._items.listIterator(startIndex)
        var end = endIndex
        while (iterator.hasNext()) {
            iterator.next()
            if (startIndex <= end) {
                iterator.remove()
                end--
            } else {
                break
            }
        }
        notifyItemRangeRemoved(startIndex, endIndex - startIndex)
    }

    fun updateLastItem(data: D) {
        this._items[this._items.size - 1] = data
        notifyItemChanged(this._items.size - 1)
    }

}