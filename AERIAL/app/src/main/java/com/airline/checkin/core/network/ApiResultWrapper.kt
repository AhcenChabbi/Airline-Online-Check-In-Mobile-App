package com.airline.checkin.core.network


sealed class ApiResultWrapper<out T> {
    data class Success<T>(val data: T) : ApiResultWrapper<T>()
    data class Error(val throwable: Throwable) : ApiResultWrapper<Nothing>()
}
