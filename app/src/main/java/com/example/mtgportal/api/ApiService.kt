package com.example.mtgportal.api

import com.example.mtgportal.api.response.CardTypesApiResponse
import com.example.mtgportal.api.response.CardsApiResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    //region API calls
    /**
     * Get list of cards with pagination
     * @param page API result page index
     * @param name Filter on card name
     * @param colors Filter on card colors
     * @param types Filter on card types
     * @param rarity Filter on card rarity
     */
    @GET("cards")
    suspend fun getCards(
        @Query("page") page: Int,
        @Query("name") name: String?,
        @Query("colors") colors: String?,
        @Query("type") types: String?,
        @Query("rarity") rarity: String?
    ): CardsApiResponse

    /**
     * Get list of all card types
     */
    @GET("types")
    suspend fun getCardTypes(): CardTypesApiResponse
    //endregion

    //region helpers
    sealed class ApiResult<out T> {
        data class Success<out T>(val value: T) : ApiResult<T>()
        data class ApiError(val code: Int? = null, val error: String?) : ApiResult<Nothing>()
        object NetworkError : ApiResult<Nothing>()
        object UnknownError : ApiResult<Nothing>()
        object JobCanceled : ApiResult<Nothing>()
    }

    companion object {
        var BASE_URL = "https://api.magicthegathering.io/v1/"
        fun create(): ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
    //endregion
}