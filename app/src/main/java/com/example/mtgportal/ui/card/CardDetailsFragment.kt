package com.example.mtgportal.ui.card

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.mtgportal.R
import com.example.mtgportal.databinding.FragmentCardDetailsBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.base.BaseFragment
import com.example.mtgportal.utils.view.setTitle

class CardDetailsFragment : BaseFragment<FragmentCardDetailsBinding>() {

    //region declaration
    private val _args: CardDetailsFragmentArgs by navArgs()
    private val _card: Card by lazy { _args.card }
    //region

    //region lifecycle
    override fun getLayoutResId(): Int = R.layout.fragment_card_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(_card.name)
        initTransition()
    }
    //endregion

    private fun initTransition() {
        postponeEnterTransition()
        sharedElementEnterTransition = (TransitionInflater.from(context).inflateTransition(android.R.transition.move))
        binding.ivCardArt.transitionName = _card.id
        Glide.with(this)
            .load(_card.imageUrl)
            .placeholder(R.drawable.img_mtg_card_background)
            .apply(RequestOptions().dontTransform())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startTransition()
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startTransition()
                    return false
                }
            })
            .into(binding.ivCardArt)
    }

    private fun startTransition() {
        startPostponedEnterTransition()
        binding.card = _card
    }
}