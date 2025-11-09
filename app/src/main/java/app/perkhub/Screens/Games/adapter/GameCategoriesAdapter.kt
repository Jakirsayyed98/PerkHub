package app.perkhub.Screens.Games.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Games.model.Data
import app.perkhub.Screens.Games.model.GameCategoryData
import app.perkhub.Screens.Games.model.GamesCategoryRes
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.GameCategoryLayoutBinding
import app.perkhub.databinding.HomeGamesLayoutBinding
import app.perkhub.databinding.LootproductLayoutBinding
import com.bumptech.glide.Glide

class GameCategoriesAdapter<S>(val clickListener: RecyclerClickListener): BaseRecyclerView<S,GameCategoriesAdapter<S>.Holder>() {

    inner class Holder(val binding: GameCategoryLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s:S){
            if (s is GameCategoryData){
                binding.categoryName.text = s.name

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
        return Holder(GameCategoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

}