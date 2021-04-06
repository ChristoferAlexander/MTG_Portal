package com.example.mtgportal.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.App
import com.example.mtgportal.R
import com.example.mtgportal.databinding.FragmentHomeBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.base.BaseFragment
import com.example.mtgportal.ui.card.CardGridItemViewHolder.CardGridItemClickListener
import com.example.mtgportal.ui.card.CardsAdapter
import com.example.mtgportal.ui.custom.PaginatedRecyclerView.OnBottomReachedListener
import com.example.mtgportal.ui.home.HomeViewModel.ViewState
import com.example.mtgportal.utils.ViewModelFactory

class HomeFragment : BaseFragment(), CardGridItemClickListener, OnBottomReachedListener {

    //region declaration
    private val _viewModel by lazy {
        ViewModelProvider(viewModelStore, ViewModelFactory(App.instance.apiService))
            .get(HomeViewModel::class.java)
    }
    private val _binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val _adapter: CardsAdapter by lazy { CardsAdapter(this) }
    //endregion

    //region lifecycle
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _viewModel.searchCards()
        _viewModel.viewState.observe(viewLifecycleOwner, _viewStateObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.cardsRv.onBottomReachedListener = this
        _binding.cardsRv.apply {
            val verticalDecorator = DividerItemDecoration(context, VERTICAL)
            val horizontalDecorator = DividerItemDecoration(context, HORIZONTAL)
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.divider, null)
            drawable?.let {
                horizontalDecorator.setDrawable(it)
                verticalDecorator.setDrawable(it)
            }
            addItemDecoration(horizontalDecorator)
            addItemDecoration(verticalDecorator)
            layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            adapter = _adapter
        }
    }
    //endregion

    //region ViewModel observers
    private val _viewStateObserver = Observer<ViewState> { viewState ->
        when (viewState) {
            is ViewState.DisplayCards -> _adapter.setItems(viewState.data)
            ViewState.Loading -> TODO()
        }
    }
    //endregion

    //region implements CardGridItemViewHolder.CardGridItemClickListener
    override fun onCardClicked(item: Card) {
        TODO("Not yet implemented")
    }

    override fun onFavoriteCardClicked(item: Card) {
        TODO("Not yet implemented")
    }
    //endregion

    //region implements PaginatedRecyclerView.OnBottomReachedListener()
    override fun onBottomReached() {
        _viewModel.loadNextPage()
    }
    //endregion

    override fun getInflatedView(): View {
        return _binding.root
    }
}