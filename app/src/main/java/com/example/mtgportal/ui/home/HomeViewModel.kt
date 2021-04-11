package com.example.mtgportal.ui.home

import androidx.lifecycle.*
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.model.Card
import com.example.mtgportal.model.helper.SearchFilter
import com.example.mtgportal.repository.CardRepository
import com.example.mtgportal.ui.home.HomeViewModel.ViewState.*
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val state: SavedStateHandle,
    private val cardRepository: CardRepository
) : ViewModel() {

    //region declaration
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()
    val viewState: LiveData<ViewState> = _viewState

    private var _cardList: MutableList<Card> = mutableListOf()
    private var _searchFilter: SearchFilter = state.get<SearchFilter>("searchFilter") ?: SearchFilter()
    private var _page = 1
    //endregion

    //region lifecycle
    init {
        search()
    }

    override fun onCleared() {
        state.set("searchFilter", _searchFilter)
        super.onCleared()
    }
    //endregion

    //region remote API access methods
    private fun search() {
        _viewState.value = Loading
        viewModelScope.launch {
            when (val result = cardRepository.getCards(_searchFilter, _page)) {
                is ApiResult.Success -> {
                    _cardList.addAll(result.value.cards.filter { it.imageUrl != null })
                    _viewState.postValue(DisplayCards(_cardList))
                }
                is ApiResult.ApiError -> TODO()
                ApiResult.NetworkError -> TODO()
                ApiResult.UnknownError -> TODO()
            }
        }
    }

    fun loadNextPage() {
        Timber.d("Loading next page..")
        _page++
        search()
    }

    fun onSearchQueryChanged(searchQuery: String?) {
        Timber.d("Search query changed: $searchQuery")
        _searchFilter.searchQuery = searchQuery
        _cardList.clear()
        _page = 1
        search()
    }
    //endregion

    //region state machine
    sealed class ViewState {
        object Loading : ViewState()
        data class DisplayCards(val data: List<Card>) : ViewState()
    }
    //endregion
}