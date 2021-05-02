package com.example.mtgportal.ui.favorite

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mtgportal.database.FavoriteCardsDao
import com.example.mtgportal.model.Card
import com.example.mtgportal.ui.favorite.FavoritesViewModel.ViewState.*
import kotlinx.coroutines.launch

class FavoritesViewModel(private val _favoriteCardDao: FavoriteCardsDao) : ViewModel() {

    //region declaration
    private val _viewState: MutableLiveData<ViewState> = MutableLiveData()
    val viewState: LiveData<ViewState> = _viewState

    private val _cards: MutableList<Card> = mutableListOf()
    //endregion

    //region public methods
    fun getFavorites() {
        viewModelScope.launch {
            _cards.clear()
            _cards.addAll(_favoriteCardDao.getAll())
            _viewState.postValue(DisplayCards(_cards))
        }
    }

    fun removeFavorite(card: Card) {
        viewModelScope.launch {
            _favoriteCardDao.delete(card)
            _cards.remove(card)
            _viewState.postValue(RemoveFromList(card))
        }
    }
    //endregion

    //regions members
    sealed class ViewState {
        data class DisplayCards(val data: List<Card>) : ViewState()
        data class RemoveFromList(val card: Card): ViewState()
    }
    //endregion
}