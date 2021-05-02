package com.example.mtgportal.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.R
import com.example.mtgportal.databinding.FragmentFavoritesBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.card.FavoriteItemViewHolder.FavoriteItemClickListener
import com.example.mtgportal.ui.card.FavoritesCardsAdapter
import com.example.mtgportal.ui.favorite.FavoritesViewModel.ViewState
import com.example.mtgportal.utils.viewModel.ViewModelFactory

class FavoritesFragment : Fragment(), FavoriteItemClickListener {

    //region declaration
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val _adapter: FavoritesCardsAdapter by lazy { FavoritesCardsAdapter(this) }
    private val _viewModel: FavoritesViewModel by activityViewModels {
        ViewModelFactory(requireActivity())
    }
    //endregion

    //region lifecycle
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardsRv.apply {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    //region ViewModel observers
    private val _viewStateObserver = Observer<ViewState> { viewState ->
        when (viewState) {
            is ViewState.DisplayCards -> _adapter.setItems(viewState.data)
            is ViewState.RemoveFromList -> _adapter.removeItem(viewState.card)
        }
    }
    //endregion

    //region implements FavoriteItemViewHolder.FavoriteItemClickListener
    override fun onUnfavored(item: Card) {
        _viewModel.removeFavorite(item)
    }
    //endregion
}