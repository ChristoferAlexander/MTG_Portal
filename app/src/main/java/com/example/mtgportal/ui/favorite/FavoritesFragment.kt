package com.example.mtgportal.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.R
import com.example.mtgportal.databinding.FragmentFavoritesBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.base.BaseFragment
import com.example.mtgportal.ui.card.FavoriteItemViewHolder.*
import com.example.mtgportal.ui.card.FavoritesCardsAdapter
import com.example.mtgportal.ui.favorite.FavoritesViewModel.*
import com.example.mtgportal.utils.ViewModelFactory

class FavoritesFragment : BaseFragment(), FavoriteItemClickListener {

    //region declaration
    private val _viewModel: FavoritesViewModel by activityViewModels {
        ViewModelFactory(requireActivity())
    }
    private val _binding by lazy { FragmentFavoritesBinding.inflate(layoutInflater) }
    private val _adapter: FavoritesCardsAdapter by lazy { FavoritesCardsAdapter(this) }
    //endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.cardsRv.apply {
            ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
                val verticalDecorator = DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL
                )
                verticalDecorator.setDrawable(it)
                addItemDecoration(verticalDecorator)
            }
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = _adapter
        }
        _viewModel.viewState.observe(viewLifecycleOwner, _viewStateObserver)
        _viewModel.getFavorites()
    }

    override fun getInflatedView(): View = _binding.root
    //endregion

    //region ViewModel observers
    private val _viewStateObserver = Observer<ViewState> { viewState ->
        when (viewState) {
            is ViewState.DisplayCards -> {
                _adapter.setItems(viewState.data)
            }
        }
    }
    //endregion

    //region implements FavoriteItemViewHolder.FavoriteItemClickListener
    override fun onUnfavored(item: Card) {
        _viewModel.removeFavorite(item)
    }
    //endregion
}