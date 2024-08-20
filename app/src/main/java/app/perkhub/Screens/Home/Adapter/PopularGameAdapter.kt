package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.Games.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.HomeGamesLayoutBinding
import app.perkhub.databinding.LootproductLayoutBinding
import com.bumptech.glide.Glide

class PopularGameAdapter<S>(val clickListener: RecyclerClickListener): BaseRecyclerView<S,PopularGameAdapter<S>.Holder>() {

    inner class Holder(val binding: HomeGamesLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s:S){
            if (s is Data){
                binding.name.text = s.name
                Glide.with(itemView.context).load(s.assets.square).into(binding.icon)
                binding.root.setOnClickListener {
                    clickListener.RecyclerClickListener(0,"",s)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(HomeGamesLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

}