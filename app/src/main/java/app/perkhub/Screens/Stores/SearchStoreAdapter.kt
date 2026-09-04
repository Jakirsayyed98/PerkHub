package app.perkhub.Screens.Stores

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Home.Model.Home.MiniApp
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.ItemSearchStoreBinding
import com.bumptech.glide.Glide

class SearchStoreAdapter<S>(private val clickListener: RecyclerClickListener) : BaseRecyclerView<S, SearchStoreAdapter<S>.Holder>() {
    inner class Holder(private val binding: ItemSearchStoreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: S?) {
            if (data !is MiniApp) {
                return
            }

            Glide.with(itemView.context).load(data.icon).circleCrop().into(binding.icon)
            binding.storeName.text = data.name
            val apiDescription = data.description
            binding.description.text =  apiDescription!!.ifBlank { "Explore products, offers, and exciting deals." }
//            binding.category.text = "Beauty & Personal Care"
            if (data.cbActive) {
                binding.cashbackTitle.text = "Upto"
                binding.cashbackValue.text = "${data.cbPercentage}"
                binding.cashbackValue.visibility = View.VISIBLE
                binding.cashbackLabel.text = "Cashback"
            } else {
                binding.cashbackTitle.text = "No"
                binding.cashbackLabel.text = "Cashback"
                binding.cashbackValue.text = ""
                binding.cashbackValue.visibility = View.GONE
            }

            if (data.popular){
                binding.featured.visibility = View.VISIBLE
            }else{
                binding.featured.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                clickListener.RecyclerClickListener(0, data.url!!, data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemSearchStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setData(list[position])
    }
}