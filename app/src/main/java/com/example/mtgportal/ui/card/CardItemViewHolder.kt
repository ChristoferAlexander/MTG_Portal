package com.example.mtgportal.ui.card

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.databinding.GridItemCardBinding
import com.example.mtgportal.databinding.LinearItemCardBinding
import com.example.mtgportal.model.Card

class CardItemViewHolder(private val _binding: ViewDataBinding) :
    RecyclerView.ViewHolder(_binding.root) {

    fun bind(isGrid: Boolean, item: Card, clickListener: CardItemClickListener) {
        when (_binding) {
            is GridItemCardBinding -> _binding.card = item
            is LinearItemCardBinding -> _binding.card = item
        }
        _binding.executePendingBindings()
    }

    interface CardItemClickListener {
        fun onCardClicked(item: Card)
        fun onFavoriteCardClicked(item: Card)
    }
}