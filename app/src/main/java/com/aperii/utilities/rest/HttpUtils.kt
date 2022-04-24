package com.aperii.utilities.rest

import com.google.gson.Gson
import retrofit2.HttpException

object HttpUtils {
    val gson = Gson()

    inline fun <reified T> HttpException.body(): T? = gson.fromJson(response()?.errorBody()?.string() ?: "{}", T::class.java)

}