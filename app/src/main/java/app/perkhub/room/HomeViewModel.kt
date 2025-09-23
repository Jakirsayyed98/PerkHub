package app.perkhub.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.perkhub.Screens.Home.Model.Home.HomeRes
import app.perkhub.api.ApiListener
import app.perkhub.api.launchSafe

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    fun getHomeData(listener: ApiListener<HomeRes>) {
        viewModelScope.launchSafe(onError = {
            listener.onError(it.message ?: "Unknown error")
        }) {
            val data = repository.getHomeData()!!
            listener.onResponse(data)
        }
    }
}
