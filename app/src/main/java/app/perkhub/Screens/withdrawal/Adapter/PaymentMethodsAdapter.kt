package app.perkhub.Screens.withdrawal.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Home.Category
import app.perkhub.Screens.withdrawal.model.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.databinding.CategoryLayoutBinding
import app.perkhub.databinding.FragmentHomeScreenBinding
import app.perkhub.databinding.PaymentMethodsLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PaymentMethodsAdapter<S>(val clickList: RecyclerClickListener):BaseRecyclerView<S,PaymentMethodsAdapter<S>.Holder>() {

    inner class Holder(val binding: PaymentMethodsLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setdata(s: S) {
            if (s is Data){

                binding.bankName.text = if (s.bank_name!=null) s.bank_name else "UPI ID"
                binding.accountNumber.text =  s.identifier
                binding.root.setOnClickListener {
                    clickList.RecyclerClickListener(0,"",s)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(PaymentMethodsLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setdata(list[position])
    }


}