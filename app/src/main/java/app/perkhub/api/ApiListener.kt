package app.perkhub.api

interface ApiListener<T> {
    fun onSuccess(t:T?,mess:String)
    fun onError(mess: String){}
    fun onResponse(t:T){
        onSuccess(t,"Success")
    }
}