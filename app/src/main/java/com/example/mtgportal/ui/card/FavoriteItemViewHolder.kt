package com.example.mtgportal.ui.card

import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.databinding.FavoriteItemCardBinding
import com.example.mtgportal.model.Card

class FavoriteItemViewHolder(private val _binding: FavoriteItemCardBinding) :
    RecyclerView.ViewHolder(_binding.root) {

    fun bind(item: Card, listener: FavoriteItemClickListener) {
        _binding.card = item
        _binding.listener = listener
        _binding.executePendingBindings()
    }

    interface FavoriteItemClickListener {
        fun onUnfavored(item: Card)
    }
}