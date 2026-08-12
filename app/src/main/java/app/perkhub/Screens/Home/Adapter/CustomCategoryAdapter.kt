package app.perkhub.Screens.Home.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.CustomCategories
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.PopularStoreLayoutBinding
import app.perkhub.databinding.TopCategoryLayoutBinding
import com.bumptech.glide.Glide

class CustomCategoryAdapter<S>(val clickListener: RecyclerClickListener):BaseRecyclerView<S,CustomCategoryAdapter<S>.Holder>() {

    inner class Holder(val binding: TopCategoryLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s : S,position : Int){
            if (s is CustomCategories){
                with(binding){

                    Glide.with(itemView.context).load(s.image).centerCrop().placeholder(R.drawable.ic_placeholder).into(logo)
                    root.setOnClickListener {
                        clickListener.RecyclerClickListener(position,s.name,s)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(TopCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(list[position],position)
    }

}