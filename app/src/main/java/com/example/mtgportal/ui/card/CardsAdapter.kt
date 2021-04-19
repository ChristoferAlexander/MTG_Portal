package com.example.mtgportal.ui.card

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mtgportal.databinding.GridItemCardBinding
import com.example.mtgportal.databinding.LinearItemCardBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.base.BaseRecyclerViewAdapter
import com.example.mtgportal.ui.card.CardItemViewHolder.*


class CardsAdapter(private val _cardGridItemClickListener: CardItemClickListener) :
    BaseRecyclerViewAdapter<Card, CardItemViewHolder>() {

    private val _grid = 1
    private val _linear = 2
    var isGrid = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            _linear -> CardItemViewHolder(LinearItemCardBinding.inflate(inflater, parent, false))
            else -> CardItemViewHolder(GridItemCardBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGrid) _grid else _linear
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        holder.bind(getItems()[position], _cardGridItemClickListener)
    }
}