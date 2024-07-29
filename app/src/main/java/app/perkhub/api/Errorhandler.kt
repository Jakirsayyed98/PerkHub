package app.perkhub.api

import kotlinx.coroutines.CoroutineExceptionHandler
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun SetErrorhandler(): CoroutineExceptionHandler {

    val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace()
        if (throwable is UnknownHostException || throwable is SocketTimeoutException){
        }
    }
    return exceptionHandler
}