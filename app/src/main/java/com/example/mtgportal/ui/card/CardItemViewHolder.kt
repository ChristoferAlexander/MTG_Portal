package com.example.mtgportal.ui.card

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.R
import com.example.mtgportal.databinding.GridItemCardBinding
import com.example.mtgportal.databinding.LinearItemCardBinding
import com.example.mtgportal.model.Card

class CardItemViewHolder(private val _binding: ViewDataBinding) :
    RecyclerView.ViewHolder(_binding.root) {

    fun bind(item: Card, listener: CardItemClickListener) {
        val favoriteBtnResId =
            if (item.isFavorite) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24
        when (_binding) {
            is GridItemCardBinding -> {
                _binding.btnFavorite.setImageResource(favoriteBtnResId)
                _binding.card = item
                _binding.listener = listener
            }
            is LinearItemCardBinding -> {
                _binding.btnFavorite.setImageResource(favoriteBtnResId)
                _binding.card = item
                _binding.listener = listener
            }
        }
        _binding.executePendingBindings()
    }

    interface CardItemClickListener {
        fun onCardClicked(item: Card)
        fun onFavoriteCardClicked(item: Card)
    }
}