package com.example.mtgportal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.model.Card
import com.example.mtgportal.repository.CardRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(private val cardRepository: CardRepository) : ViewModel() {

    //region remote API access methods
    fun searchCards() {
        viewModelScope.launch {
            when (val result = cardRepository.getCards()) {
                is ApiResult.Success -> {Timber.d(result.value.toString())}
                is ApiResult.ApiError -> TODO()
                ApiResult.NetworkError -> TODO()
                ApiResult.UnknownError -> TODO()
            }
        }
    }
    //endregion

    //region state machine
    sealed class ViewState {
        object Loading : ViewState()
        data class DisplayCards(val data: List<Card>) : ViewState()
    }
    //endregion
}