package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.LootProduct.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.LootproductLayoutBinding
import app.perkhub.databinding.TrendingStoresBinding
import com.bumptech.glide.Glide

class LootProductAdapter<S>(val clickListner:RecyclerClickListener): BaseRecyclerView<S,LootProductAdapter<S>.Holder>() {

    inner class Holder(val binding: LootproductLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S?) {
            if (s is Data){
                Glide.with(itemView.context).load(s.image).into(binding.productImage)
                binding.store.text = s.title
                binding.disprice.text = s.dis_price
                binding.price.text = s.price
                binding.layout.setOnClickListener {
                    clickListner.RecyclerClickListener(0,s.url,s)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LootproductLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

}