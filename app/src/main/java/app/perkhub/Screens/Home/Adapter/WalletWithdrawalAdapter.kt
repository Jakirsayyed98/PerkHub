package app.perkhub.Screens.Home.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.perkhub.R
import app.perkhub.Screens.Home.Model.Withdrawal.Data
import app.perkhub.Utils.BaseRecyclerView
import app.perkhub.Utils.RecyclerClickListener
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.databinding.WithdrawalHistoryLayoutBinding
import com.bumptech.glide.Glide

class WalletWithdrawalAdapter<S>(val clickListener: RecyclerClickListener): BaseRecyclerView<S,WalletWithdrawalAdapter<S>.Holder>() {

    inner class Holder(val binding : WithdrawalHistoryLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun setData(s: S) {
            if (s is Data){
                binding.TxnDate.text = onlyDatewithMonth( s.created_at)
                if (s.status=="0"){
                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orange))
                    binding.statusText.text = "Pending"
                    binding.rejectortxn.visibility = View.GONE
                }else if (s.status =="1"){
                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))
                    binding.statusText.text = "Transffered"
                    binding.rejectortxn.visibility = View.VISIBLE
                    binding.rejectortxn.text = itemView.context.getString(R.string.txn_id, s.txn_id)
                }else{
                    binding.statusText.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))
                    binding.statusText.text = "Rejected"
                    binding.rejectortxn.visibility = View.VISIBLE
                    binding.rejectortxn.text = itemView.context.getString(R.string.reject_reason, s.reason)
                }
                Glide.with(itemView.context).load(R.drawable.baseline_account_balance_wallet_24).centerCrop().into(binding.storeIcon)
                binding.title.text = itemView.context.getString(R.string.withdrawal_request_of_200, setSuffixAmount(s.requested_amt))
                binding.amt.text = itemView.context.getString(R.string.withdrawal_amount,
                    setSuffixAmount(s.requested_amt)
                )
                binding.vpa.text = itemView.context.getString(R.string.vpa, s.VPA_Id)


                binding.root.setOnClickListener {
                    clickListener.RecyclerClickListener(0,"transaction",s)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(WithdrawalHistoryLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        return holder.setData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}