package app.perkhub.room

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.perkhub.api.MyApi
import app.perkhub.api.RequestViewModel
import app.perkhub.room.Home.HomeRepository

class RequestViewModelFactory(
    private val repository: HomeRepository,
    private val apiProvider: (Context) -> MyApi
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestViewModel(repository, apiProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

