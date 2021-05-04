package com.example.mtgportal.repository

import com.example.mtgportal.api.ApiService
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.api.response.CardTypesApiResponse

class CardTypesRepository private constructor(private val _apiService: ApiService) : BaseRepository() {

    suspend fun getRemoteCardTypes(): ApiResult<CardTypesApiResponse> {
        return safeApiCall { _apiService.getCardTypes() }
    }

    companion object {
        @Volatile
        private var INSTANCE: CardTypesRepository? = null
        fun getInstance(apiService: ApiService): CardTypesRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CardTypesRepository(apiService).also { INSTANCE = it }
            }
    }
}