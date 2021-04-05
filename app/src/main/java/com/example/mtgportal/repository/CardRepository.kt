package com.example.mtgportal.repository

import com.example.mtgportal.api.ApiService
import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.api.response.CardTypesApiResponse
import com.example.mtgportal.api.response.CardsApiResponse

class CardRepository(private val apiService: ApiService) : BaseRepository() {
    suspend fun getCards(): ApiResult<CardsApiResponse> {
        return safeApiCall { apiService.getCards(0, "", "", "", "") }
    }

    suspend fun getCardTypes(): ApiResult<CardTypesApiResponse> {
        return safeApiCall { apiService.getCardTypes() }
    }
}