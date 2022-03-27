package com.aperii.api.result

import retrofit2.HttpException

sealed class ApiResult<out T> {

    object Loading : ApiResult<Nothing>()
    data class Success<out V>(val data: V) : ApiResult<V>()
    data class Error(val e: HttpException) : ApiResult<Nothing>()

}