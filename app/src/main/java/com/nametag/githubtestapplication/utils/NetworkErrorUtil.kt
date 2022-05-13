package com.nametag.githubtestapplication.utils

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import org.koin.android.BuildConfig
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


fun <T> rxParseError(): Function<Throwable, Observable<T>> {
    return Function { throwable: Throwable -> Observable.error(parseError(throwable)) }
}

fun <T> rxParseErrorSingle(): Function<Throwable, Single<T>> {
    return Function { throwable: Throwable -> Single.error(parseError(throwable)) }
}

fun rxParseErrorCompletable(): Function<Throwable, Completable> {
    return Function { throwable: Throwable -> Completable.error(parseError(throwable)) }
}

fun <T> parseErrorObservable(e: HttpException): Observable<T> {
    return Observable.error(parseError(e))
}

private fun parseError(throwable: Throwable): NetworkException {
    return when {
        throwable is HttpException -> {
            when (throwable.code()) {
                in SERVER_ERROR_CODE_START..SERVER_ERROR_CODE_END -> {
                    NetworkException.ServerException(throwable.code())
                }
                AUTH_ERROR_CODE -> NetworkException.AuthException()
                VALIDATION_ERROR -> NetworkException.ValidationException()
                else -> {
                    if (BuildConfig.DEBUG) {
                        NetworkException.Undefined("${throwable.code()} ${throwable.message()} ${throwable.response()}")
                    } else {
                        NetworkException.Undefined("Try again later")
                    }
                }
            }
        }
        isConnectionProblem(throwable) -> {
            NetworkException.NoNetworkException()
        }
        isServerConnectionProblem(throwable) -> {
            NetworkException.ServerConnectionException()
        }
        else -> {
            NetworkException.Undefined(throwable.message)
        }
    }
}

private fun isServerConnectionProblem(throwable: Throwable?): Boolean {
    return throwable is SocketException || throwable is SocketTimeoutException
}

private fun isConnectionProblem(throwable: Throwable?): Boolean {
    return throwable is UnknownHostException || throwable is ConnectException
}


private const val SERVER_ERROR_CODE_START = 500
private const val SERVER_ERROR_CODE_END = 505
private const val AUTH_ERROR_CODE = 401
private const val VALIDATION_ERROR = 422

private const val USER_ERROR_CODE = 447
private const val TAG = "NetworkErrorUtils"


sealed class NetworkException(mError: String?) : Exception(mError) {
    data class Undefined(val error: String?) :
        NetworkException(error)

    data class ServerException(val code: Int, val error: String = "Try again later") :
        NetworkException(error)

    data class ServerConnectionException(val error: String? = "Try again later") :
        NetworkException(error)

    data class NoNetworkException(val error: String = "No internet connection") :
        NetworkException(error)

    data class AuthException(val code: Int = 401, val error: String = "No authorized") :
        NetworkException(error)

    data class ValidationException(
        val code: Int = 422,
        val error: String = "need validate request"
    ) :
        NetworkException(error)

    data class ApiException(val code: Int, val error: String) :
        NetworkException(error)
}