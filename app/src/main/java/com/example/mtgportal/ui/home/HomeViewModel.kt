package com.example.mtgportal.ui.home

import androidx.lifecycle.*
import com.example.mtgportal.api.ApiService.ApiResult.*
import com.example.mtgportal.model.Card
import com.example.mtgportal.model.definitions.MtgColors
import com.example.mtgportal.model.definitions.MtgRarities
import com.example.mtgportal.model.helper.SearchFilter
import com.example.mtgportal.repository.CardsRepository
import com.example.mtgportal.repository.CardTypesRepository
import com.example.mtgportal.repository.FavoritesCardsRepository
import com.example.mtgportal.ui.home.HomeViewModel.ViewState.*
import com.example.mtgportal.utils.liveData.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val _state: SavedStateHandle,
    private val _cardsRepository: CardsRepository,
    private val _cardTypesRepository: CardTypesRepository,
    private val _favoritesCardsRepository: FavoritesCardsRepository
) : ViewModel() {

    //region declaration
    // -- view observers --
    private val _viewStateMutableLiveData: MutableLiveData<ViewState> = MutableLiveData()
    val viewStateLiveData: LiveData<ViewState> = _viewStateMutableLiveData
    private val _cardTypesMutableLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val cardTypesLiveData: LiveData<List<String>> = _cardTypesMutableLiveData
    private val _errorViewStateMutableLiveData: MutableLiveData<Event<ErrorViewState>> = MutableLiveData()
    val errorStateLiveData: LiveData<Event<ErrorViewState>> = _errorViewStateMutableLiveData

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
            _favoriteCards.addAll(_favoritesCardsRepository.getFavorites())
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
        _viewStateMutableLiveData.value = Loading
        apiJob = viewModelScope.launch {
            when (val response = _cardsRepository.getRemoteCards(searchFilter, _page)) {
                is Success -> {
                    val remoteCards = response.value.cards.filter { it.imageUrl != null }
                    remoteCards.intersect(_favoriteCards).forEach { it.isFavorite = true }
                    _cards.addAll(remoteCards)
                    if (_cards.isEmpty()) _viewStateMutableLiveData.postValue(NoResultFound)
                    else _viewStateMutableLiveData.postValue(DisplayResult(_cards))
                }
                NetworkError -> _errorViewStateMutableLiveData.postValue(Event(ErrorViewState.DisplayNetworkError))
                is ApiError -> _errorViewStateMutableLiveData.postValue(Event(ErrorViewState.DisplayApiError(response.error)))
                is UnknownError -> _errorViewStateMutableLiveData.postValue(Event(ErrorViewState.DisplayUnknownError(response.error)))
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
        if (_cardTypesMutableLiveData.value == null)
            viewModelScope.launch {
                when (val response = _cardTypesRepository.getRemoteCardTypes()) {
                    is Success -> _cardTypesMutableLiveData.postValue(response.value.types)
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
                    _favoritesCardsRepository.addFavorite(card)
                }
                false -> {
                    _favoriteCards.remove(card)
                    _favoritesCardsRepository.deleteFavorite(card)
                }
            }
            _cards.find { it.id == card.id }?.isFavorite = card.isFavorite
            _viewStateMutableLiveData.postValue(DisplayResult(_cards))
        }
    }

    fun loadNextPage() {
        Timber.i("Loading next page..")
        _page++
        search()
    }

    fun refresh() {
        Timber.d("Refreshing favorite cards..")
        viewModelScope.launch {
            _favoriteCards.clear()
            _favoriteCards.addAll(_favoritesCardsRepository.getFavorites())
            when {
                _cards.isEmpty() -> search()
                else -> {
                    _cards.subtract(_favoriteCards).forEach { it.isFavorite = false }
                    _viewStateMutableLiveData.postValue(DisplayResult(_cards))
                }
            }
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

    sealed class ErrorViewState {
        object DisplayNetworkError : ErrorViewState()
        class DisplayApiError(val message: String?) : ErrorViewState()
        class DisplayUnknownError(val message: String?) : ErrorViewState()
    }
    //endregion
}