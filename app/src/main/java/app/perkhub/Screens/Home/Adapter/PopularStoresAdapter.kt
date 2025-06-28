package app.perkhub.Screens.Home.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.CategoryLayoutBinding
import app.perkhub.databinding.PopularAppLayoutBinding
import com.bumptech.glide.Glide

class PopularStoresAdapter<S>(val clickListener: RecyclerClickListener):BaseRecyclerView<S,PopularStoresAdapter<S>.Holder>() {

    inner class Holder(val binding: PopularAppLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s : S,position : Int){
            if (s is MiniApp){
                with(binding){

                    Glide.with(itemView.context).load(s.icon ?: s.logo).centerCrop().placeholder(R.drawable.ic_placeholder).into(logo)
                    name.text = s.name
                    cbtext.text =if (s.cbPercentage?.isNotBlank() == true) {
                        "Up to ${s.cbPercentage}% Cashback"
                    } else {
                        s.label ?: ""
                    }
                    root.setOnClickListener {
                        clickListener.RecyclerClickListener(position,s.name,s)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(PopularAppLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(list[position],position)
    }

}