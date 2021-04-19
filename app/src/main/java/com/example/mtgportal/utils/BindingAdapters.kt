package com.example.mtgportal.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

@BindingAdapter(
    value = ["remoteImageUrl", "remoteImagePlaceholderResId", "remoteImageCornerRadius"],
    requireAll = false
)
fun setRemoteImageUrl(
    view: AppCompatImageView,
    remoteImageUrl: String?,
    remoteImagePlaceholderResId: Int?,
    remoteImageCornerRadius: Int?
) {
    remoteImageUrl?.let {
        remoteImageCornerRadius?.let {
            Glide.with(view.context)
                .load(remoteImageUrl)
                .placeholder(remoteImagePlaceholderResId ?: 0) //TODO change with default image placeholder
                .transform((RoundedCorners(it)))
                .into(view)
        } ?: run{
            Glide.with(view.context)
                .load(remoteImageUrl)
                .transform((RoundedCorners(15)))
                .placeholder(remoteImagePlaceholderResId ?: 0) //TODO change with default image placeholder
                .into(view)
        }
    }
}