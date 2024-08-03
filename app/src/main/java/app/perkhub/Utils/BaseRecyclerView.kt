package app.perkhub.Utils

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerView <T,I:RecyclerView.ViewHolder>:RecyclerView.Adapter<I>() {
    var list = ArrayList<T>()

    fun addAllItem(list:List<T>){
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(data:T){
        this.list.add(data)
        notifyDataSetChanged()
    }

    fun clear(){
        this.list.clear()
        notifyDataSetChanged()
    }

}