package com.example.mtgportal.ui.home

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.HORIZONTAL
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.*
import com.example.mtgportal.R
import com.example.mtgportal.databinding.FragmentHomeBinding
import com.example.mtgportal.model.Card
import com.example.mtgportal.model.definitions.MtgColors
import com.example.mtgportal.model.definitions.MtgRarities
import com.example.mtgportal.ui.base.BaseFragment
import com.example.mtgportal.ui.card.CardItemViewHolder.CardItemClickListener
import com.example.mtgportal.ui.card.CardsAdapter
import com.example.mtgportal.ui.custom.PaginatedRecyclerView.OnBottomReachedListener
import com.example.mtgportal.ui.dialog.*
import com.example.mtgportal.ui.home.HomeViewModel.*
import com.example.mtgportal.ui.home.HomeViewModel.ErrorViewState.*
import com.example.mtgportal.utils.context.getAppName
import com.example.mtgportal.utils.liveData.Event
import com.example.mtgportal.utils.view.setTitle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.sharedStateViewModel


class HomeFragment :
    BaseFragment<FragmentHomeBinding>(),
    CardItemClickListener,
    OnBottomReachedListener,
    OnRefreshListener {

    //region declaration
    private val _viewModel: HomeViewModel by sharedStateViewModel()

    private val _adapter: CardsAdapter by lazy { CardsAdapter(this) }
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
        postponeEnterTransition()
        _viewModel.searchFilter.searchQuery?.let { setTitle("\"$it\"") }
        initializeBottomSheet()
        initializeRecyclerView()
        initBinding()
        subscribeObservers()
    }

    override fun onResume() {
        super.onResume()
        _viewModel.refresh()
    }
    //endregion

    //region options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        initSearchUi(menu.findItem(R.id.action_search))
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val itemGrid = menu.findItem(R.id.action_toggle_grid)
        val drawableRes = if (_viewModel.isGridDisplay) R.drawable.ic_grid_on_24 else R.drawable.ic_grid_off_24
        itemGrid.icon = ResourcesCompat.getDrawable(resources, drawableRes, null)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_toggle_grid -> {
            _viewModel.toggleGridState()
            updateAdapterGridState()
            activity?.invalidateOptionsMenu()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
    //endregion

    //region init methods
    private fun initializeBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetRoot)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun initBinding() {
        binding.clickListener = _viewClickListener
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    private fun subscribeObservers() {
        _viewModel.viewStateLiveData.observe(viewLifecycleOwner, _viewStateObserver)
        _viewModel.errorStateLiveData.observe(viewLifecycleOwner, _errorViewStateObserver)
    }

    private fun initSearchUi(menuItem: MenuItem) {
        (menuItem.actionView as SearchView).let { searchView ->
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.isIconified = true
                    searchView.clearFocus()
                    menuItem.collapseActionView()
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

    //region private helper methods
    private fun updateAdapterGridState() {
        _adapter.isGrid = _viewModel.isGridDisplay
        binding.cardsRv.layoutManager = when {
            _viewModel.isGridDisplay -> GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            else -> LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun handleFilterToggle(view: View, @MtgColors color: String) {
        val isFilterToggled = _viewModel.toggleColor(color)
        view.alpha = if (isFilterToggled) 1F else 0.5F
    }

    private fun resetLoaders() {
        binding.noResult.visibility = GONE
        binding.progressBar.visibility = GONE
        binding.swipeRefresh.isRefreshing = false
    }
    //endregion

    //region ViewModel observers
    private val _viewStateObserver = Observer<ViewState> { viewState ->
        resetLoaders()
        when (viewState) {
            is ViewState.DisplayResult -> {
                if (viewState.data.isNotEmpty()) _adapter.setItems(viewState.data)
                (view?.parent as? ViewGroup)?.doOnPreDraw { startPostponedEnterTransition() }
            }
            ViewState.Loading -> binding.progressBar.visibility = VISIBLE
            ViewState.NoResultFound -> {
                _adapter.clearItems()
                binding.noResult.visibility = VISIBLE
            }
        }
    }

    private val _errorViewStateObserver = Observer<Event<ErrorViewState>> { errorViewState ->
        resetLoaders()
        when (val error = errorViewState.getContentIfNotHandled()) {
            is DisplayApiError -> createErrorDialog(errorMessage = error.message).show(childFragmentManager, DIALOG_TAG_ERROR)
            is DisplayUnknownError -> createErrorDialog(errorMessage = error.message).show(childFragmentManager, DIALOG_TAG_ERROR)
            DisplayNetworkError -> createNetworkErrorDialog().show(childFragmentManager, DIALOG_TAG_NETWORK_ERROR)
        }
    }
    //endregion

    //region click listeners
    private val _viewClickListener = View.OnClickListener {
        val isFilterToggled: Boolean
        when (it.id) {
            R.id.peak_button -> bottomSheetBehavior.state =
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
            R.id.filter_colorless -> handleFilterToggle(binding.bottomSheet.filterColorless, MtgColors.COLORLESS)
            R.id.filter_white -> handleFilterToggle(binding.bottomSheet.filterWhite, MtgColors.WHITE)
            R.id.filter_black -> handleFilterToggle(binding.bottomSheet.filterBlack, MtgColors.BLACK)
            R.id.filter_blue -> handleFilterToggle(binding.bottomSheet.filterBlue, MtgColors.BLUE)
            R.id.filter_red -> handleFilterToggle(binding.bottomSheet.filterRed, MtgColors.RED)
            R.id.filter_green -> handleFilterToggle(binding.bottomSheet.filterGreen, MtgColors.GREEN)
            R.id.filter_rarity_common -> handleFilterToggle(binding.bottomSheet.filterRarityCommon, MtgRarities.COMMON)
            R.id.filter_rarity_uncommon -> handleFilterToggle(binding.bottomSheet.filterRarityUncommon, MtgRarities.UNCOMMON)
            R.id.filter_rarity_rare -> handleFilterToggle(binding.bottomSheet.filterRarityRare, MtgRarities.RARE)
            R.id.filter_rarity_mythic_rare -> handleFilterToggle(binding.bottomSheet.filterRarityMythicRare, MtgRarities.MYTHIC_RARE)
        }
    }
    //endregion

    //region implements CardGridItemViewHolder.CardGridItemClickListener
    override fun onCardClicked(item: Card, imageView: AppCompatImageView) {
        val extras = FragmentNavigatorExtras(imageView to imageView.transitionName)
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToCardDetailsFragment(
                item
            ), extras
        )
    }

    override fun onFavoriteCardClicked(item: Card) = _viewModel.toggleFavorite(item)
    //endregion

    //region implements SwipeRefreshLayout.OnRefreshListener
    override fun onRefresh() {
        _viewModel.refresh()
    }
    //endregion

    //region implements PaginatedRecyclerView.OnBottomReachedListener()
    override fun onBottomReached() = _viewModel.loadNextPage()
    //endregion
}