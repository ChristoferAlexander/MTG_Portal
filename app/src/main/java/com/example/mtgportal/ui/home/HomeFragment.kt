package com.example.mtgportal.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtgportal.R
import com.example.mtgportal.databinding.FragmentHomeBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.MainActivity
import com.example.mtgportal.ui.base.BaseFragment
import com.example.mtgportal.ui.card.CardItemViewHolder.CardItemClickListener
import com.example.mtgportal.ui.card.CardsAdapter
import com.example.mtgportal.ui.custom.PaginatedRecyclerView.OnBottomReachedListener
import com.example.mtgportal.ui.home.HomeViewModel.ViewState
import com.example.mtgportal.utils.ViewModelFactory


class HomeFragment : BaseFragment(), CardItemClickListener, OnBottomReachedListener {

    //region declaration
    private val _viewModel: HomeViewModel by activityViewModels {
        ViewModelFactory(requireActivity())
    }
    private val _binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val _adapter: CardsAdapter by lazy { CardsAdapter(this) }
    //endregion

    //region lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.cardsRv.onBottomReachedListener = this
        _binding.cardsRv.apply {
            ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
                val verticalDecorator = DividerItemDecoration(context, VERTICAL)
                val horizontalDecorator = DividerItemDecoration(context, HORIZONTAL)
                horizontalDecorator.setDrawable(it)
                verticalDecorator.setDrawable(it)
                addItemDecoration(horizontalDecorator)
                addItemDecoration(verticalDecorator)
            }
            layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            adapter = _adapter
        }
        _viewModel.viewState.observe(viewLifecycleOwner, _viewStateObserver)
    }

    override fun onResume() {
        super.onResume()
        _viewModel.refreshFavorite()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
        (menu.findItem(R.id.action_search).actionView as SearchView).let { searchView ->
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.isIconified = true
                    searchView.clearFocus()
                    menu.findItem(R.id.action_search).collapseActionView()
                    (activity as MainActivity?)?.supportActionBar?.title =
                        if (query.isNullOrBlank()) null else "\"$query\""
                    _viewModel.onSearchQueryChanged(if (query.isNullOrBlank()) null else query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            searchView.setOnCloseListener {
                (activity as MainActivity?)?.supportActionBar?.title = ""
                _viewModel.onSearchQueryChanged(null)
                false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_toggle_grid -> {
            _adapter.toggleGrid()
            item.icon =
                if (_adapter.isGrid) ResourcesCompat.getDrawable(resources, R.drawable.ic_grid_on_24, null)
                else ResourcesCompat.getDrawable(resources, R.drawable.ic_grid_off_24, null)
            _binding.cardsRv.layoutManager =
                if (_adapter.isGrid) GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
                else LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun getInflatedView(): View = _binding.root
    //endregion

    //region ViewModel observers
    private val _viewStateObserver = Observer<ViewState> { viewState ->
        when (viewState) {
            is ViewState.DisplayCards -> {
                _binding.progressBar.visibility = GONE
                _adapter.setItems(viewState.data)
            }
            ViewState.Loading -> _binding.progressBar.visibility = VISIBLE
        }
    }
    //endregion

    //region implements CardGridItemViewHolder.CardGridItemClickListener
    override fun onCardClicked(item: Card) {
        //TODO
    }

    override fun onFavoriteCardClicked(item: Card) {
        _viewModel.toggleFavorite(item)
    }
    //endregion

    //region implements PaginatedRecyclerView.OnBottomReachedListener()
    override fun onBottomReached() {
        _viewModel.loadNextPage()
    }
    //endregion
}