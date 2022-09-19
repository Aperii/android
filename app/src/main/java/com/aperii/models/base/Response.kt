package com.aperii.models.base


sealed interface Response<out T> {

    class Success<out T>(val data: T) : Response<T>

    class Error<T, E>(val error: E) : Response<T>

    class Failure<T>(val th: Throwable) : Response<T>

    fun ifSuccessful(callback: (T) -> Unit) {
        if (this is Success) {
            callback(data)
        }
    }

    fun <E> fold(
        onSuccess: ((T) -> Unit)? = null,
        onError: ((E) -> Unit)? = null,
        onFailure: ((Throwable) -> Unit)? = null
    ) {
        when (this) {
            is Success -> onSuccess?.invoke(data)
            is Error<*, *> -> onError?.invoke(error as E)
            is Failure -> onFailure?.invoke(th)
        }
    }

    fun bodyOrNull(): T? {
        return if (this is Success)
            data
        else
            null
    }

}