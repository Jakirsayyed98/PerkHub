package app.perkhub.Screens.Offers.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.Screens.Offers.model.OffersData
import app.perkhub.Screens.Profile.model.menuList
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.copyToClipboard
import app.perkhub.databinding.MenuListItemBinding
import app.perkhub.databinding.OfferListLayoutBinding
import com.bumptech.glide.Glide

class OffersListAdapter<S>(val clickList: RecyclerClickListener):BaseRecyclerView<S,OffersListAdapter<S>.Holder>() {

    inner class Holder(val binding: OfferListLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setdata(s: S) {
            if (s is OffersData){

                binding.storename.text = s.store_name
                binding.title.text = s.title
                binding.dis.text =  Html.fromHtml(s.description, Html.FROM_HTML_MODE_LEGACY)
                if (s.type.equals("coupon")){
                    binding.couponlayout.visibility = View.VISIBLE
                    binding.couponCode.text = s.coupon_code
                }else{
                    binding.couponlayout.visibility = View.GONE
                }

                binding.copy.setOnClickListener {
                    copyToClipboard(itemView.context,binding.couponCode.text.toString(),binding.couponlayout)
                }

                Glide.with(itemView.context)
                    .load(s.image)
                    .circleCrop()
                    .into(binding.icon)
                binding.shop.setOnClickListener {
                    clickList.RecyclerClickListener(0,s.store_id.toString(),s)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(OfferListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setdata(list[position])
    }


}