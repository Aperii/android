package com.aperii.utilities.rest

import com.aperii.api.error.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response

object HttpUtils {
    val gson = Gson()

    inline fun <reified T> HttpException.body(): T? =
        gson.fromJson(response()?.errorBody()?.string() ?: "{}", T::class.java)

    fun <T> Response<T>.ifSuccessful(block: (T) -> Unit) = fold<T, ErrorResponse>({}, block)

    inline fun <T, reified E> Response<T>.fold(
        onError: (E) -> Unit,
        onSuccess: (T) -> Unit
    ) {
        try {
            if (isSuccessful)
                body()?.let(onSuccess)
            else
                onError(gson.fromJson(errorBody()?.string() ?: "{}", E::class.java))
        } catch (e: Throwable) {
        }
    }

    inline fun <T, reified E> getResponse(apiCall: (() -> Response<T>)): com.aperii.models.base.Response<T> {
        return try {
            val res = apiCall()
            if (res.isSuccessful)
                com.aperii.models.base.Response.Success(res.body()!!)
            else
                com.aperii.models.base.Response.Error(
                    RestAPI.gson.fromJson(res.errorBody()!!.string(), E::class.java)
                )
        } catch (e: Throwable) {
            com.aperii.models.base.Response.Failure(e)
        }
    }
}