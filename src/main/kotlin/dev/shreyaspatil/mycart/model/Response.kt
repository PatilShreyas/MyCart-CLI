package dev.shreyaspatil.mycart.model

sealed class Response<T> {
    data class Success<T>(val data: T) : Response<T>()
    data class Error<T>(val message: String) : Response<T>()
}