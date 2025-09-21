package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.TrendingStoresBannerBinding
import app.perkhub.databinding.TrendingStoresBinding
import com.bumptech.glide.Glide

class TrendingStoresAdapter1<S>(val cliklistner : RecyclerClickListener): BaseRecyclerView<S,TrendingStoresAdapter1<S>.Holder>() {

    inner class Holder(val binding: TrendingStoresBannerBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S) {
            if (s is MiniApp){
                with(binding){

                    Glide.with(itemView.context).load(s.banner).into(icon)
//                    rupee.visibility =if (s.cbActive)  View.VISIBLE else View.GONE
//                    if (s.cbActive){
//                        cashbackText.visibility= View.VISIBLE
//                    }else{
//                        cashbackText.visibility= View.INVISIBLE
//                    }
//                    storename.text = s.name
//                    cashbackText.text = "Upto "+s.cbPercentage+" cashback"
                    binding.root.setOnClickListener {
                        cliklistner.RecyclerClickListener(0,s.url,s)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(TrendingStoresBannerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(list[position])
    }

}