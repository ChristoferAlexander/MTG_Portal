package com.example.mtgportal.ui.home

import androidx.lifecycle.*
import com.example.mtgportal.api.ApiService.ApiResult.*
import com.example.mtgportal.model.Card
import com.example.mtgportal.model.definitions.MtgColors
import com.example.mtgportal.model.definitions.MtgRarities
import com.example.mtgportal.model.helper.SearchFilter
import com.example.mtgportal.repository.CardRepository
import com.example.mtgportal.ui.home.HomeViewModel.ViewState.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val _state: SavedStateHandle,
    private val _cardRepository: CardRepository
) : ViewModel() {

    //region declaration
    private val _viewStateLiveData: MutableLiveData<ViewState> = MutableLiveData()
    val viewStateLiveData: LiveData<ViewState> = _viewStateLiveData
    private val _cardTypesLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val cardTypeLiveData: LiveData<List<String>> = _cardTypesLiveData

    // --api helpers--
    private var apiJob: Job? = null
    private var _page = 1

    // --card caching--
    private var _cards: MutableList<Card> = mutableListOf()
    private var _favoriteCards: MutableList<Card> = mutableListOf()

    // --saved state--
    private val searchFilterKey = "searchFilter"
    private val isGridDisplayKey = "isGridDisplay"
    var searchFilter: SearchFilter = _state.get(searchFilterKey) ?: SearchFilter()
    var isGridDisplay: Boolean = _state.get(isGridDisplayKey) ?: true
    //endregion

    //region lifecycle
    init {
        getCardTypes()
        viewModelScope.launch {
            _favoriteCards.addAll(_cardRepository.getFavorites())
            search()
        }
    }

    override fun onCleared() {
        _state.set(searchFilterKey, searchFilter)
        _state.set(isGridDisplayKey, isGridDisplay)
        super.onCleared()
    }
    //endregion

    //region private methods
    private fun search() {
        Timber.i("Searching..")
        _viewStateLiveData.value = Loading
        apiJob = viewModelScope.launch {
            when (val response = _cardRepository.getRemoteCards(searchFilter, _page)) {
                is Success -> {
                    val remoteCards = response.value.cards.filter { it.imageUrl != null }
                    remoteCards.intersect(_favoriteCards).forEach { it.isFavorite = true }
                    _cards.addAll(remoteCards)
                    if (_cards.isEmpty()) _viewStateLiveData.postValue(NoResultFound)
                    else _viewStateLiveData.postValue(DisplayResult(_cards))
                }
                is ApiError -> TODO()
                NetworkError -> TODO()
                UnknownError -> TODO()
            }
        }
    }

    private fun resetAndSearch() {
        Timber.i("Resetting search..")
        apiJob?.cancel()
        _cards.clear()
        _page = 1
        search()
    }

    private fun getCardTypes() {
        if (_cardTypesLiveData.value == null)
            viewModelScope.launch {
                when (val response = _cardRepository.getRemoteTypes()) {
                    is Success -> _cardTypesLiveData.postValue(response.value.types)
                }
            }
    }
    //endregion

    //region public methods
    fun onSearchQueryChanged(searchQuery: String?) {
        searchFilter.searchQuery = searchQuery
        resetAndSearch()
    }

    fun toggleColor(@MtgColors color: String): Boolean {
        val isToggled = searchFilter.colorFilters.contains(color)
        if (isToggled) searchFilter.colorFilters.remove(color)
        else searchFilter.colorFilters.add(color)
        resetAndSearch()
        return !isToggled
    }

    fun toggleRarity(@MtgRarities rarity: String): Boolean {
        val isToggled = searchFilter.rarityFilters.contains(rarity)
        if (isToggled) searchFilter.rarityFilters.remove(rarity)
        else searchFilter.rarityFilters.add(rarity)
        resetAndSearch()
        return !isToggled
    }

    fun toggleFavorite(card: Card) {
        Timber.d("Toggling favorite status for $card")
        card.isFavorite = !card.isFavorite
        viewModelScope.launch {
            when (card.isFavorite) {
                true -> {
                    _favoriteCards.add(card)
                    _cardRepository.addFavorite(card)
                }
                false -> {
                    _favoriteCards.remove(card)
                    _cardRepository.deleteFavorite(card)
                }
            }
            _cards.find { it.id == card.id }?.isFavorite = card.isFavorite
            _viewStateLiveData.postValue(DisplayResult(_cards))
        }
    }

    fun loadNextPage() {
        Timber.i("Loading next page..")
        _page++
        search()
    }

    fun refreshFavorite() {
        Timber.d("Refreshing favorite cards..")
        viewModelScope.launch {
            _favoriteCards.clear()
            _favoriteCards.addAll(_cardRepository.getFavorites())
            _cards.subtract(_favoriteCards).forEach { it.isFavorite = false }
            _viewStateLiveData.postValue(DisplayResult(_cards))
        }
    }

    fun toggleGridState() {
        isGridDisplay = !isGridDisplay
    }
    //endregion

    //regions members
    sealed class ViewState {
        object Loading : ViewState()
        data class DisplayResult(val data: List<Card>) : ViewState()
        object NoResultFound : ViewState()
    }
    //endregion
}