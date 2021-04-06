package com.example.mtgportal.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.model.Card
import com.example.mtgportal.repository.CardRepository
import com.example.mtgportal.ui.home.HomeViewModel.ViewState.DisplayCards
import kotlinx.coroutines.launch

class HomeViewModel(private val cardRepository: CardRepository) : ViewModel() {

    //region declaration
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()
    val viewState: LiveData<ViewState> = _viewState
    //endregion

    //region remote API access methods
    fun searchCards() {
        viewModelScope.launch {
            when (val result = cardRepository.getCards()) {
                is ApiResult.Success -> {_viewState.postValue(DisplayCards(result.value.cards))}
                is ApiResult.ApiError -> TODO()
                ApiResult.NetworkError -> TODO()
                ApiResult.UnknownError -> TODO()
            }
        }
    }

    fun loadNextPage() {
        TODO("Not yet implemented")
    }
    //endregion

    //region state machine
    sealed class ViewState {
        object Loading : ViewState()
        data class DisplayCards(val data: List<Card>) : ViewState()
    }
    //endregion
}