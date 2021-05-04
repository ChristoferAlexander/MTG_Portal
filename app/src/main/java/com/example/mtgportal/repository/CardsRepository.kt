package com.example.mtgportal.repository

import com.example.mtgportal.api.ApiService
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.api.response.CardsApiResponse
import com.example.mtgportal.model.helper.SearchFilter
import timber.log.Timber

class CardsRepository private constructor(private val _apiService: ApiService) : BaseRepository() {

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


    companion object {
        @Volatile
        private var INSTANCE: CardsRepository? = null

        fun getInstance(apiService: ApiService): CardsRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CardsRepository(apiService).also { INSTANCE = it }
            }
    }
}