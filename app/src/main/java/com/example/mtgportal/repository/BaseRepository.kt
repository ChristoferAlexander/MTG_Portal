package com.example.mtgportal.repository

import com.example.mtgportal.api.ApiService.ApiResult
import com.example.mtgportal.api.ApiService.ApiResult.*
import retrofit2.HttpException
import java.io.IOException

open class BaseRepository {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResult<T> {
        return try {
            Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> NetworkError
                is HttpException -> ApiError(throwable.code(), throwable.message())
                else -> UnknownError
            }
        }
    }
}
