package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.ActiveCategoryStoresBinding
import app.perkhub.databinding.TrendingStoresBinding
import com.bumptech.glide.Glide

class TrendingStoresAdapter2<S>(val cliklistner : RecyclerClickListener): BaseRecyclerView<S,TrendingStoresAdapter2<S>.Holder>() {

    inner class Holder(val binding: ActiveCategoryStoresBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S) {
            if (s is MiniApp){
                with(binding){

                    Glide.with(itemView.context).load(s.icon).circleCrop().into(icon)
                    if (s.cbActive){
                        cashbackBadge.visibility= View.VISIBLE
                    }else{
                        cashbackBadge.visibility= View.INVISIBLE
                    }
                    storename.text = s.name+":"
                    binding.root.setOnClickListener {
                        cliklistner.RecyclerClickListener(0,s.url,s)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ActiveCategoryStoresBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(list[position])
    }

}