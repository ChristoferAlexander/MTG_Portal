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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import com.example.mtgportal.ui.dialog.DialogFactory
import com.example.mtgportal.ui.dialog.DialogFactory.DIALOG_TAG_ERROR
import com.example.mtgportal.ui.dialog.DialogFactory.DIALOG_TAG_NETWORK_ERROR
import com.example.mtgportal.ui.home.HomeViewModel.*
import com.example.mtgportal.ui.home.HomeViewModel.ErrorViewState.*
import com.example.mtgportal.utils.viewModel.ViewModelFactory
import com.example.mtgportal.utils.context.getAppName
import com.example.mtgportal.utils.liveData.Event
import com.example.mtgportal.utils.view.setTitle
import com.google.android.material.bottomsheet.BottomSheetBehavior


class HomeFragment :
    BaseFragment<FragmentHomeBinding>(),
    CardItemClickListener,
    OnBottomReachedListener,
    OnRefreshListener {

    //region declaration
    private val _viewModel: HomeViewModel by activityViewModels { ViewModelFactory(requireActivity()) }
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
        initializeBottomSheet()
        initializeRecyclerView()
        _viewModel.searchFilter.searchQuery?.let { setTitle("\"it\"") }
        _viewModel.viewStateLiveData.observe(viewLifecycleOwner, _viewStateObserver)
        _viewModel.errorStateLiveData.observe(viewLifecycleOwner, _errorViewStateObserver)
        binding.clickListener = _viewClickListener
        binding.swipeRefresh.setOnRefreshListener(this)
    }

    override fun onResume() {
        super.onResume()
        _viewModel.refresh()
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

    private fun toggleSearchFilterAlpha(view: View, isEnabled: Boolean) {
        view.alpha = if (isEnabled) 1F else 0.5F
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
        //TODO move dialogs to factory class
        when (val error = errorViewState.getContentIfNotHandled()) {
            is DisplayApiError -> DialogFactory.createErrorDialog(errorMessage = error.message).show(childFragmentManager, DIALOG_TAG_ERROR)
            is DisplayUnknownError -> DialogFactory.createErrorDialog(errorMessage = error.message).show(childFragmentManager, DIALOG_TAG_ERROR)
            DisplayNetworkError -> DialogFactory.createNetworkErrorDialog().show(childFragmentManager, DIALOG_TAG_NETWORK_ERROR)
        }
    }
    //endregion

    //region click listeners
    private val _viewClickListener = View.OnClickListener {
        val isFilterToggled: Boolean
        when (it.id) {
            R.id.peak_button -> bottomSheetBehavior.state =
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED
                else BottomSheetBehavior.STATE_EXPANDED
            R.id.filter_colorless -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.COLORLESS)
                toggleSearchFilterAlpha(binding.bottomSheet.filterColorless, isFilterToggled)
            }
            R.id.filter_white -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.WHITE)
                toggleSearchFilterAlpha(binding.bottomSheet.filterWhite, isFilterToggled)
            }
            R.id.filter_black -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.BLACK)
                toggleSearchFilterAlpha(binding.bottomSheet.filterBlack, isFilterToggled)
            }
            R.id.filter_blue -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.BLUE)
                toggleSearchFilterAlpha(binding.bottomSheet.filterBlue, isFilterToggled)
            }
            R.id.filter_red -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.RED)
                toggleSearchFilterAlpha(binding.bottomSheet.filterRed, isFilterToggled)
            }
            R.id.filter_green -> {
                isFilterToggled = _viewModel.toggleColor(MtgColors.GREEN)
                toggleSearchFilterAlpha(binding.bottomSheet.filterGreen, isFilterToggled)
            }
            R.id.filter_rarity_common -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.COMMON)
                toggleSearchFilterAlpha(binding.bottomSheet.filterRarityCommon, isFilterToggled)
            }
            R.id.filter_rarity_uncommon -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.UNCOMMON)
                toggleSearchFilterAlpha(binding.bottomSheet.filterRarityUncommon, isFilterToggled)
            }
            R.id.filter_rarity_rare -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.RARE)
                toggleSearchFilterAlpha(binding.bottomSheet.filterRarityRare, isFilterToggled)
            }
            R.id.filter_rarity_mythic_rare -> {
                isFilterToggled = _viewModel.toggleRarity(MtgRarities.MYTHIC_RARE)
                toggleSearchFilterAlpha(binding.bottomSheet.filterRarityMythicRare, isFilterToggled)
            }
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