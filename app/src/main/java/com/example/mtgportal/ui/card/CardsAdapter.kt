package com.example.mtgportal.ui.card

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mtgportal.databinding.GridItemCardBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.base.BaseRecyclerViewAdapter
import com.example.mtgportal.ui.card.CardGridItemViewHolder.*

class CardsAdapter(private val _cardGridItemClickListener: CardGridItemClickListener) :
    BaseRecyclerViewAdapter<Card, CardGridItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardGridItemViewHolder {
        return CardGridItemViewHolder(
            GridItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardGridItemViewHolder, position: Int) {
        holder.bind(getItems()[position], _cardGridItemClickListener)
    }
}