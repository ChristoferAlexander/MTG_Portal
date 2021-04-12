package com.example.mtgportal.ui.home

import androidx.lifecycle.*
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.api.ApiService.ApiResult.*
import com.example.mtgportal.model.Card
import com.example.mtgportal.model.helper.SearchFilter
import com.example.mtgportal.repository.CardRepository
import com.example.mtgportal.ui.home.HomeViewModel.ViewState.*
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val _state: SavedStateHandle,
    private val _cardRepository: CardRepository
) : ViewModel() {

    //region declaration
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()
    val viewState: LiveData<ViewState> = _viewState

    private var _cards: MutableList<Card> = mutableListOf()
    private var _favoriteCards: MutableList<Card> = mutableListOf()
    private var _searchFilter: SearchFilter =
        _state.get<SearchFilter>("searchFilter") ?: SearchFilter()
    private var _page = 1
    //endregion

    //region lifecycle
    init {
        viewModelScope.launch {
            _favoriteCards.addAll(_cardRepository.getFavorites())
            search()
        }
    }

    override fun onCleared() {
        _state.set("searchFilter", _searchFilter)
        super.onCleared()
    }
    //endregion

    //region private methods
    private fun search() {
        _viewState.value = Loading
        viewModelScope.launch {
            when (val result = _cardRepository.getRemote(_searchFilter, _page)) {
                is Success -> {
                    val remoteCards = result.value.cards.filter { it.imageUrl != null }
                    remoteCards.intersect(_favoriteCards).forEach { it.isFavorite = true }
                    _cards.addAll(remoteCards)
                    _viewState.postValue(DisplayCards(_cards))
                }
                is ApiError -> TODO()
                NetworkError -> TODO()
                UnknownError -> TODO()
            }
        }
    }
    //endregion

    //region public methods
    fun loadNextPage() {
        Timber.d("Loading next page..")
        _page++
        search()
    }

    fun onSearchQueryChanged(searchQuery: String?) {
        Timber.d("Search query changed: $searchQuery")
        _searchFilter.searchQuery = searchQuery
        _cards.clear()
        _page = 1
        search()
    }

    fun toggleFavorite(card: Card) {
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
            _viewState.postValue(DisplayCards(_cards))
        }
    }

    fun refreshFavorite() {
        viewModelScope.launch {
            _favoriteCards.clear()
            _favoriteCards.addAll(_cardRepository.getFavorites())
            _cards.subtract(_favoriteCards).forEach { it.isFavorite = false }
            _viewState.postValue(DisplayCards(_cards))
        }
    }
    //endregion

    //regions members
    sealed class ViewState {
        object Loading : ViewState()
        data class DisplayCards(val data: List<Card>) : ViewState()
    }
    //endregion
}