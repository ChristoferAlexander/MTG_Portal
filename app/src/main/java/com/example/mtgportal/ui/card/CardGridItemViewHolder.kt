package com.example.mtgportal.ui.card

import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.databinding.GridItemCardBinding
import com.example.mtgportal.model.Card

class CardGridItemViewHolder(private val _binding: GridItemCardBinding) :
    RecyclerView.ViewHolder(_binding.root) {

    fun bind(item: Card, clickListener: CardGridItemClickListener) {
        _binding.card = item
        _binding.executePendingBindings()
    }

    interface CardGridItemClickListener {
        fun onCardClicked(item: Card)
        fun onFavoriteCardClicked(item: Card)
    }
}