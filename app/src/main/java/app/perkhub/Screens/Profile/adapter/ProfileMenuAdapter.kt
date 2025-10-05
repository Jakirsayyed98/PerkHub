package app.perkhub.Screens.Profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Profile.model.menuList
import app.perkhub.Screens.withdrawal.model.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.CategoryLayoutBinding
import app.perkhub.databinding.FragmentHomeScreenBinding
import app.perkhub.databinding.MenuListItemBinding
import app.perkhub.databinding.PaymentMethodsLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ProfileMenuAdapter<S>(val clickList: RecyclerClickListener):BaseRecyclerView<S,ProfileMenuAdapter<S>.Holder>() {

    inner class Holder(val binding: MenuListItemBinding):RecyclerView.ViewHolder(binding.root){
        fun setdata(s: S) {
            if (s is menuList){

                binding.title.text = s.title
                binding.description.text = s.description
                Glide.with(itemView.context)
                    .load(s.image)
                    .circleCrop()
                    .into(binding.icon)
                binding.root.setOnClickListener {
                    clickList.RecyclerClickListener(0,s.id.toString(),s)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(MenuListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setdata(list[position])
    }


}