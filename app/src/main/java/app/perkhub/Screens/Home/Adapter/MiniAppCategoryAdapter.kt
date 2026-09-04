package app.perkhub.Screens.Home.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.LootproductLayoutBinding
import app.perkhub.databinding.MiniappListLayoutBinding
import com.bumptech.glide.Glide

class MiniAppCategoryAdapter<S>(val clickListener: RecyclerClickListener):BaseRecyclerView<S,MiniAppCategoryAdapter<S>.Holder>() {

    inner class Holder(val binding: MiniappListLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun setData(s: S?) {
            if ( s is MiniApp){
                Glide.with(itemView.context).load(s.icon).circleCrop().into(binding.icon)
                binding.name.text = s.name

                binding.cb.text =if (s.cbActive) "Upto "+s.cbPercentage+" Cashback" else "Cashback is not active"
                binding.root.setOnClickListener {
                    clickListener.RecyclerClickListener(0,s.url!!,s)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder( MiniappListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

}