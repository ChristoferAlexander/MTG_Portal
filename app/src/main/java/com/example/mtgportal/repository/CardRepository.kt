package com.example.mtgportal.repository

import com.example.mtgportal.api.ApiService
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.api.response.CardTypesApiResponse
import com.example.mtgportal.api.response.CardsApiResponse
import com.example.mtgportal.database.FavoriteCardsDao
import com.example.mtgportal.model.Card
import com.example.mtgportal.model.helper.SearchFilter
import timber.log.Timber

class CardRepository private constructor(
    private val _apiService: ApiService,
    private val _favoriteCardsDao: FavoriteCardsDao
) : BaseRepository() {

    suspend fun getRemoteCards(searchFilter: SearchFilter, page: Int): ApiResult<CardsApiResponse> {
        Timber.i("Requesting cards matching: $searchFilter")
        Timber.i("Page: $page")
        return safeApiCall {
            _apiService.getCards(
                page = page,
                name = searchFilter.searchQuery,
                colors = searchFilter.colorFilters.joinToString { it }.replace(" ", ""),
                types = null,
                rarity = searchFilter.rarityFilters.joinToString { it }.replace(" ", "")
            )
        }
    }

    suspend fun getRemoteTypes(): ApiResult<CardTypesApiResponse> {
        return safeApiCall { _apiService.getCardTypes() }
    }

    suspend fun getFavorites(): List<Card> {
        Timber.i("Fetching favorites from database..")
        return _favoriteCardsDao.getAll()
    }

    suspend fun addFavorite(card: Card) {
        Timber.i("Card added to favorite")
        _favoriteCardsDao.insert(card)
    }

    suspend fun deleteFavorite(card: Card) {
        _favoriteCardsDao.delete(card)
    }

    companion object {
        @Volatile
        private var INSTANCE: CardRepository? = null

        fun getInstance(
            apiService: ApiService,
            favoriteCardsDao: FavoriteCardsDao
        ): CardRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CardRepository(apiService, favoriteCardsDao).also { INSTANCE = it }
            }
    }
}