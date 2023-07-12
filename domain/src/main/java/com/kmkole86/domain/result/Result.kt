package com.kmkole86.domain.result

sealed interface Result<out R> {

    data class Success<R>(val data: R) : Result<R>
    data class Error(val exception: Throwable? = null) : Result<Nothing>
}