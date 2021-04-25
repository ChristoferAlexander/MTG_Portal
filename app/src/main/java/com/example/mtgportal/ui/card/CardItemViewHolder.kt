package com.example.mtgportal.ui.card

import androidx.appcompat.widget.AppCompatImageView
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
            if (item.isFavorite) R.drawable.ic_tap_red_24 else R.drawable.ic_tap_24
        val rotation = if (item.isFavorite) 45F else 0F
        when (_binding) {
            is GridItemCardBinding -> {
                _binding.ivCardArt.transitionName = item.id
                _binding.btnFavorite.setImageResource(favoriteBtnResId)
                _binding.btnFavorite.rotation = rotation
                _binding.card = item
                _binding.root.setOnClickListener {
                    listener.onCardClicked(item, _binding.ivCardArt)
                }
                _binding.listener = listener
            }
            is LinearItemCardBinding -> {
                _binding.ivCardArt.transitionName = item.id
                _binding.btnFavorite.setImageResource(favoriteBtnResId)
                _binding.btnFavorite.rotation = rotation
                _binding.card = item
                _binding.root.setOnClickListener {
                    listener.onCardClicked(item, _binding.ivCardArt)
                }
                _binding.listener = listener
            }
        }
        _binding.executePendingBindings()
    }

    interface CardItemClickListener {
        fun onCardClicked(item: Card, imageView: AppCompatImageView)
        fun onFavoriteCardClicked(item: Card)
    }
}