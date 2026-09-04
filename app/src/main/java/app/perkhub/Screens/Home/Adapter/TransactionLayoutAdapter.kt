package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.copyToClipboard
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.databinding.TransactionLayoutBinding
import com.bumptech.glide.Glide

class TransactionLayoutAdapter<S>(val clickListener: RecyclerClickListener):BaseRecyclerView<S,TransactionLayoutAdapter<S>.Holder>() {

    inner class Holder(val binding: TransactionLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S) {
            if (s is Data){

                binding.TxnDate.text = onlyDatewithMonth( s.created_at)
                if (s.status=="pending"){
//                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_pending_bg))
                    binding.statusBg.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_pending_bg))
                    binding.statusText.text = "Pending"
                    binding.trackingDescription.text = "We'll update you as soon as there's progress"
                    Glide.with(itemView.context).load(R.drawable.ic_clock).centerCrop().into(binding.statusIcon)
                }else if (s.status =="verified"){
//                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_verified_bg))
                    binding.statusBg.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.status_verified_bg))
                    binding.statusText.text = "Verified"
                    binding.trackingDescription.text = "Your order has been verified successfully."
                    Glide.with(itemView.context).load(R.drawable.verified_logo).centerCrop().into(binding.statusIcon)
                }else{
//                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.rejected_icon_bg))
                    binding.statusBg.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.rejected_icon_bg))
                    binding.statusText.text = "Rejected"
                    binding.trackingDescription.text = "Unfortunately, your order could not be verified."
                    Glide.with(itemView.context).load(R.drawable.baseline_cancel_24).centerCrop().into(binding.statusIcon)
                }

                Glide.with(itemView.context).load(s.icon).centerCrop().into(binding.storeIcon)
                binding.storeName.text = s.miniapp_name

                binding.copyTxnId.setOnClickListener {
                    copyToClipboard(itemView.context,binding.TxnID.text.toString(),binding.root)
                }

                binding.TxnID.text = itemView.context.getString(R.string.txn_id,s.transaction_id)
                binding.SaleAmount.text = setSuffixAmount(s.sale_amount)//itemView.context.getString(R.string.sale_amount,setSuffixAmount(s.sale_amount))
                binding.commision.text = setSuffixAmount(s.user_commission)//itemView.context.getString(R.string.commission_amount,setSuffixAmount(s.user_commission))
                binding.root.setOnClickListener {
                    clickListener.RecyclerClickListener(0,"transaction",s)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(TransactionLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }


}