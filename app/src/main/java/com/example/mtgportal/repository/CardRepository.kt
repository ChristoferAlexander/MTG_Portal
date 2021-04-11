package com.example.mtgportal.repository

import com.example.mtgportal.api.ApiService
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.api.response.CardTypesApiResponse
import com.example.mtgportal.api.response.CardsApiResponse
import com.example.mtgportal.model.helper.SearchFilter
import timber.log.Timber

class CardRepository private constructor(private val apiService: ApiService) : BaseRepository() {
    suspend fun getCards(searchFilter: SearchFilter, page: Int): ApiResult<CardsApiResponse> {
        Timber.d("Requesting cards matching: $searchFilter")
        Timber.d("Page: $page")
        return safeApiCall {
            apiService.getCards(
                page = page,
                name = searchFilter.searchQuery,
                colors = null,
                types = null,
                rarity = null
            )
        }
    }

    suspend fun getCardTypes(): ApiResult<CardTypesApiResponse> {
        return safeApiCall { apiService.getCardTypes() }
    }

    companion object {
        @Volatile
        private var INSTANCE: CardRepository? = null

        fun getInstance(apiService: ApiService): CardRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CardRepository(apiService).also { INSTANCE = it }
            }
    }
}