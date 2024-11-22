package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.databinding.TransactionLayoutBinding
import com.bumptech.glide.Glide

class TransactionLayoutAdapter<S>(val clickListener: RecyclerClickListener):BaseRecyclerView<S,TransactionLayoutAdapter<S>.Holder>() {

    inner class Holder(val binding: TransactionLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S) {
            if (s is Data){

                binding.TxnDate.text = onlyDatewithMonth( s.created_at)
                if (s.status=="0"){
                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.orange))
                    binding.statusText.text = "Pending"
                }else if (s.status =="1"){
                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.green))
                    binding.statusText.text = "Verified"
                }else{
                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.red))
                    binding.statusText.text = "Rejected"
                }
                if (s.miniApp!=null){
                    Glide.with(itemView.context).load(s.miniApp[0].icon).centerCrop().into(binding.storeIcon)
                    binding.storeName.text = s.miniApp[0].name
                }

                binding.TxnID.text = itemView.context.getString(R.string.txn_id,s.transaction_id)
                binding.SaleAmount.text = itemView.context.getString(R.string.sale_amount,setSuffixAmount(s.sale_amount))
                binding.Commision.text = itemView.context.getString(R.string.commission_amount,setSuffixAmount(s.user_commission))
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