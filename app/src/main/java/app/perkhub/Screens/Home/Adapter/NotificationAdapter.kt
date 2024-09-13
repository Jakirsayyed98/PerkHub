package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.Notification.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.databinding.NotificationLayoutBinding
import com.bumptech.glide.Glide

class NotificationAdapter<S>(val clickListener: RecyclerClickListener): BaseRecyclerView<S,NotificationAdapter<S>.Holder>() {

    inner class Holder(val binding:NotificationLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S) {
            if (s is Data){
                binding.title.text = s.title
                binding.date.text = onlyDatewithMonth(s.created_at)
                if (s.icon.equals("")){

                }else{
                    Glide.with(itemView.context).load(s.icon).circleCrop().into(binding.icon)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(NotificationLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}