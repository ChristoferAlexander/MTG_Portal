package com.example.mtgportal.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mtgportal.R

@BindingAdapter(
    value = ["remoteImageUrl"],
    requireAll = false
)
fun setRemoteImageUrl(
    view: AppCompatImageView,
    remoteImageUrl: String
) {
    remoteImageUrl.let {
        Glide.with(view.context)
            .load(remoteImageUrl)
            .placeholder(R.drawable.img_mtg_card_background)
            .apply(RequestOptions().dontTransform())
            .into(view)
    }
}
