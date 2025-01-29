package com.app.easy_patient.wrappers

import java.io.IOException

sealed class Resource<out R> {
    data class Success<out T>(val data: T) : Resource<T>()
    object Empty : Resource<Nothing>()
    data class Error(val exception: Throwable) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Empty -> "Empty"
            Loading -> "Loading"
        }
    }
}

suspend fun <T> callApi(apiCall: suspend () -> T): Resource<T> {
    return try {
        val response = apiCall.invoke()
        if (response.isEmptyResponse()) {
            Resource.Empty
        } else {
            Resource.Success(response)
        }
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> Resource.Error(NoInternetException())
            else -> Resource.Error(throwable)
        }
    }
}

fun <T> Resource<T>.successOr(fallback: T): T {
    return (this as? Resource.Success<T>)?.data ?: fallback
}

fun <T> Resource<T>.succeeded(): Boolean {
    return this is Resource.Success<T>
}

val <T> Resource<T>.data: T?
    get() = (this as? Resource.Success<T>)?.data

fun <T> T.isEmptyResponse(): Boolean {
    return this != null && this is List<*> && this.isEmpty()
}

class NoInternetException() : IOException() {
    override val message: String
        get() = "No Internet Available"
}
