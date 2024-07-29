package app.perkhub.api

data class Resource<out T>(
    val status: Status,val data:T?,val message:String?
){
companion object{
    fun <T> onSuccess (data:T?):Resource<T>{
        return Resource(Status.Success,data,"Success")
    }

    fun <T> onError(msg :String?,data:T?):Resource<T>{
        return Resource(Status.Error,data, msg)
    }

    fun <T> loading(data:T?):Resource<T>{
        return Resource(Status.Loading,data,"Loading")
    }

}
}
