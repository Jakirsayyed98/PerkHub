package app.perkhub.Screens.Games.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.HomeGamesLayoutBinding
import app.perkhub.databinding.LootproductLayoutBinding
import com.bumptech.glide.Glide

class TrendingGameAdapter<S>(val clickListener: RecyclerClickListener): BaseRecyclerView<S,TrendingGameAdapter<S>.Holder>() {

    inner class Holder(val binding: HomeGamesLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s:S){
            if (s is Data){
                binding.tvGameName.text = s.name
                binding.tvPlays.text = "${formatPlays(s.gamePlays)} plays"
                Glide.with(itemView.context).load(s.assets.cover).into(binding.imgCover)
                binding.root.setOnClickListener {
                    clickListener.RecyclerClickListener(0,"",s)
                }
            }
        }
    }

    fun formatPlays(count: String): String {
        val n = count.toLongOrNull() ?: 0
        return when {
            n >= 1_000_000 -> "${n / 1_000_000}M"
            n >= 1_000 -> "${n / 1_000}K"
            else -> "$n"
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