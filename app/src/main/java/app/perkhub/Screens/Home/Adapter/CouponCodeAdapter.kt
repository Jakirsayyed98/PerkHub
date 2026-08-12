package app.perkhub.Screens.Home.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.Offers.model.OffersData
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.copyToClipboard
import app.perkhub.Utils.formatDate
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.databinding.CategoryLayoutBinding
import app.perkhub.databinding.FragmentHomeScreenBinding
import app.perkhub.databinding.LayoutCouponTicketBinding
import com.airbnb.lottie.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class CouponCodeAdapter<S>(val clickList: RecyclerClickListener):BaseRecyclerView<S,CouponCodeAdapter<S>.Holder>() {

    inner class Holder(val binding: LayoutCouponTicketBinding):RecyclerView.ViewHolder(binding.root){
        fun setdata(s: S) {
            if (s is OffersData){
                Glide.with(itemView.context).load(s.image).placeholder(R.drawable.ic_placeholder)
                    .into(binding.imgLogo)
                binding.txtOffer.text = s.title
                Log.d("@@@@@@@@",formatDate(s.end_date)!!)
                binding.validTill.text= "Valid till: "+formatDate(s.end_date)
                binding.txtCode.text = "CODE: "+s.coupon_code.trim()
                val txt = binding.txtCode.text.toString().replace("CODE: ","")
                binding.imgCopy.setOnClickListener {
                    copyToClipboard(itemView.context,txt,binding.txtCode)
                }
                binding.root.setOnClickListener {
                    clickList.RecyclerClickListener(0,"",s)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutCouponTicketBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setdata(list[position])
    }


}