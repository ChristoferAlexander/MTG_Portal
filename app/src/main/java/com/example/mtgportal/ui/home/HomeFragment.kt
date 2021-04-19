package com.example.mtgportal.ui.home

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.example.mtgportal.model.definitions.MtgColors
import com.example.mtgportal.model.definitions.MtgRarities
import com.example.mtgportal.ui.base.BaseFragment
import com.example.mtgportal.ui.card.CardItemViewHolder.CardItemClickListener
import com.example.mtgportal.ui.card.CardsAdapter
import com.example.mtgportal.ui.custom.PaginatedRecyclerView.OnBottomReachedListener
import com.example.mtgportal.ui.home.HomeViewModel.ViewState
import com.example.mtgportal.utils.ViewModelFactory
import com.example.mtgportal.utils.getAppName
import com.example.mtgportal.utils.setTitle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback


class HomeFragment : BaseFragment<FragmentHomeBinding>(), CardItemClickListener,
    OnBottomReachedListener {

    //region declaration
    private val _adapter: CardsAdapter by lazy { CardsAdapter(this) }
    private val _viewModel: HomeViewModel by activityViewModels { ViewModelFactory(requireActivity()) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    //endregion

    override fun getLayoutResId(): Int = R.layout.fragment_home

    //region lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewModel.searchFilter.searchQuery?.let { setTitle("\"it\"") }
        initializeBottomSheet()
        initializeRecyclerView()
        _viewModel.viewState.observe(viewLifecycleOwner, _viewStateObserver)
        binding.clickListener = _viewClickListener
    }

    override fun onResume() {
        super.onResume()
        _viewModel.refreshFavorite()
    }
    //endregion

    //region options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        initSearchUi(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemGrid = menu.findItem(R.id.action_toggle_grid)
        itemGrid.icon =
            if (_viewModel.isGridDisplay)
                ResourcesCompat.getDrawable(resources, R.drawable.ic_grid_on_24, null)
            else
                ResourcesCompat.getDrawable(resources, R.drawable.ic_grid_off_24, null)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_toggle_grid -> {
            _viewModel.toggleGridState()
            updateAdapterGridState()
            activity?.invalidateOptionsMenu()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
    //endregion

    //region UI init methods
    private fun initSearchUi(menu: Menu) {
        (menu.findItem(R.id.action_search).actionView as SearchView).let { searchView ->
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.isIconified = true
                    searchView.clearFocus()
                    menu.findItem(R.id.action_search).collapseActionView()
                    setTitle(if (query.isNullOrEmpty()) context?.getAppName() else "\"$query\"")
                    _viewModel.onSearchQueryChanged(if (query.isNullOrBlank()) null else query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
            searchView.setOnCloseListener {
                setTitle(context?.getAppName())
                _viewModel.onSearchQueryChanged(null)
                false
            }
        }
    }

    private fun initializeBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetRoot)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (isAdded) {
                    binding.bottomSheet.iconTap.rotation = slideOffset * 90
                }
            }
        })
    }

    private fun initializeRecyclerView() {
        updateAdapterGridState()
        binding.cardsRv.onBottomReachedListener = this
        binding.cardsRv.apply {
            ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
                val verticalDecorator = DividerItemDecoration(context, VERTICAL)
                val horizontalDecorator = DividerItemDecoration(context, HORIZONTAL)
                horizontalDecorator.setDrawable(it)
                verticalDecorator.setDrawable(it)
                addItemDecoration(horizontalDecorator)
                addItemDecoration(verticalDecorator)
            }
            adapter = _adapter
        }
    }
    //endregion

    //region UI manipulation methods
    private fun updateAdapterGridState() {
        _adapter.isGrid = _viewModel.isGridDisplay
        binding.cardsRv.layoutManager = when {
            _viewModel.isGridDisplay -> GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            else -> LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun toggleSearchFilter(view: View, isEnabled: Boolean) {
        view.alpha = if (isEnabled) 1F else 0.5F
    }
    //endregion

    //region ViewModel observers
    private val _viewStateObserver = Observer<ViewState> { viewState ->
        when (viewState) {
            is ViewState.DisplayCards -> {
                binding.progressBar.visibility = GONE
                _adapter.setItems(viewState.data)
            }
            ViewState.Loading -> binding.progressBar.visibility = VISIBLE
        }
    }
    //endregion

    //region click listeners
    private val _viewClickListener = View.OnClickListener {
        var isFilterToggled = false
        when (it.id) {
            R.id.peak_button -> bottomSheetBehavior.state =
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED
                else BottomSheetBehavior.STATE_EXPANDED
            R.id.filer_white -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.WHITE)
                toggleSearchFilter(binding.bottomSheet.filerWhite, isFilterToggled)
            }
            R.id.filter_black -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.BLACK)
                toggleSearchFilter(binding.bottomSheet.filterBlack, isFilterToggled)
            }
            R.id.filter_blue -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.BLUE)
                toggleSearchFilter(binding.bottomSheet.filterBlue, isFilterToggled)
            }
            R.id.filter_red -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.RED)
                toggleSearchFilter(binding.bottomSheet.filterRed, isFilterToggled)
            }
            R.id.filter_green -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.GREEN)
                toggleSearchFilter(binding.bottomSheet.filterGreen, isFilterToggled)
            }
            R.id.filter_rarity_common -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.COMMON)
                toggleSearchFilter(binding.bottomSheet.filterRarityCommon, isFilterToggled)
            }
            R.id.filter_rarity_uncommon -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.UNCOMMON)
                toggleSearchFilter(binding.bottomSheet.filterRarityUncommon, isFilterToggled)
            }
            R.id.filter_rarity_rare -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.RARE)
                toggleSearchFilter(binding.bottomSheet.filterRarityRare, isFilterToggled)
            }
            R.id.filter_rarity_mythic_rare -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.MYTHIC_RARE)
                toggleSearchFilter(binding.bottomSheet.filterRarityMythicRare, isFilterToggled)
            }
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