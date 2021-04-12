package com.example.mtgportal.ui.card

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.mtgportal.databinding.FavoriteItemCardBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.base.BaseRecyclerViewAdapter
import com.example.mtgportal.ui.card.FavoriteItemViewHolder.FavoriteItemClickListener

class FavoritesCardsAdapter(private val _listener: FavoriteItemClickListener) :
    BaseRecyclerViewAdapter<Card, FavoriteItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return FavoriteItemViewHolder(FavoriteItemCardBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteItemViewHolder, position: Int) {
        holder.bind(getItems()[position], _listener)
    }
}