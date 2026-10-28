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

    inner class Holder(private val binding: TransactionLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(s: S) {
            if (s !is Data) return
            val context = itemView.context
            val status = when (s.status.lowercase()) {
                "pending" -> {
                    StatusConfig(title = "Pending",title2="Order Tracked", description = "We'll update you as soon as there's progress", textColor = R.color.orange, backgroundColor = R.color.status_note_stroke, icon = R.drawable.outline_history_24)
                }

                "verified" -> {
                    StatusConfig(title = "Verified",title2="Cashback is Verified", description = "Your order has been verified successfully.", textColor = R.color.green, backgroundColor = R.color.status_verified_bg, icon = R.drawable.baseline_check_circle_24)
                }

                else -> {
                    StatusConfig(title = "Rejected",title2="Cashback is Rejected", description = "Unfortunately, your order could not be verified.", textColor = R.color.red, backgroundColor = R.color.red_100, icon = R.drawable.baseline_cancel_24)
                }
            }

            // Transaction date
            binding.TxnDate.text = onlyDatewithMonth(s.created_at)

            // Status UI
            binding.statusText.text = status.title
            binding.trackingTitle.text = status.title2
            binding.trackingDescription.text = status.description
            binding.statusText.setTextColor(ContextCompat.getColor(context, status.textColor))
            binding.statusBg.setBackgroundColor(ContextCompat.getColor(context, status.backgroundColor))
            binding.trackingIconBG.setCardBackgroundColor(ContextCompat.getColor(context, status.backgroundColor))

            // Status icon
            Glide.with(context).load(status.icon).centerCrop().into(binding.statusIcon)
            Glide.with(context).load(status.icon).centerCrop().into(binding.trackingIcon)



            // Transaction details
            Glide.with(context).load(s.icon).centerCrop().into(binding.storeIcon)

            val imageColor = ContextCompat.getColor(context, status.textColor)
            binding.statusIcon.setColorFilter(imageColor)
            binding.trackingIcon.setColorFilter(imageColor)

            binding.storeName.text = s.miniapp_name
            binding.TxnID.text = context.getString(R.string.txn_id, s.transaction_id)
            binding.SaleAmount.text = setSuffixAmount(s.sale_amount)
            binding.commision.text = setSuffixAmount(s.user_commission)

            // Copy transaction ID
            binding.copyTxnId.setOnClickListener {
                copyToClipboard(context, s.transaction_id, binding.root)
            }

            // Open transaction details
            binding.root.setOnClickListener {
                clickListener.RecyclerClickListener(0, "transaction", s)
            }
        }
    }

    private data class StatusConfig(val title: String, val title2: String, val description: String, val textColor: Int, val backgroundColor: Int, val icon: Int)

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