package com.example.mtgportal.utils

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

@BindingAdapter(
    value = ["remoteImageUrl", "remoteImagePlaceholder", "remoteImageCornerRadius"],
    requireAll = false
)
fun setRemoteImageUrl(
    view: AppCompatImageView,
    remoteImageUrl: String?,
    remoteImagePlaceholderResId: Int?,
    cornerRadius: Int?
) {
    remoteImageUrl?.let {
        cornerRadius?.let {
            Glide.with(view.context)
                .load(remoteImageUrl)
                .placeholder(remoteImagePlaceholderResId ?: 0) //TODO change with default image placeholder
                .transform((RoundedCorners(it)))
                .into(view)
        } ?: run{
            Glide.with(view.context)
                .load(remoteImageUrl)
                .placeholder(remoteImagePlaceholderResId ?: 0) //TODO change with default image placeholder
                .into(view)
        }
    }
}