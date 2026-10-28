package app.perkhub.Screens.StoreTransaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import app.perkhub.ContainerActivity
import app.perkhub.R
import app.perkhub.Screens.Home.Model.TransactionModel.Data
import app.perkhub.Screens.Profile.model.CreateNewTicketRequest
import app.perkhub.Utils.BaseFragment
import app.perkhub.Utils.DATA
import app.perkhub.Utils.copyToClipboard
import app.perkhub.Utils.onlyDatewithMonth
import app.perkhub.Utils.setSuffixAmount
import app.perkhub.databinding.FragmentTransactionDetailBinding
import com.bumptech.glide.Glide
import com.google.gson.Gson


class TransactionDetailFragment : BaseFragment<FragmentTransactionDetailBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionDetailBinding.inflate(layoutInflater,container,false)
        _binding!!.toolbar.backbtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher?.onBackPressed()
        }
        _binding!!.toolbar.name.text = "Transaction Detail"

        val data =Gson().fromJson<Data>( requireActivity().intent?.getStringExtra(DATA),Data::class.java)
        setData(data)




        return _binding?.root
    }

    private fun setData(data: Data?) {

        _binding!!.storename.text = data!!.miniapp_name
        Glide.with(requireContext()).load(data.icon).into(_binding!!.logo)

        _binding!!.transactiondate.text = onlyDatewithMonth(data.created_at)
        _binding!!.transactionId.text ="Transaction Id:- #TX"+ data.transaction_id
        _binding!!.transactionAmt.text = setSuffixAmount(data.sale_amount)
        _binding!!.commision.text = setSuffixAmount(data.user_commission)

        _binding!!.transactionIdContainer.setOnClickListener {
            copyToClipboard(requireContext(), data.transaction_id,binding.root)
        }

        _binding!!.helpCard.setOnClickListener {
            val orderId = data.transaction_id
            val subject = "Shopping Cashback Issue – Order ID: $orderId"
            val description = """
                    Cashback not credited for Order ID: $orderId. Please check the cashback status and help resolve this issue.
                """.trimIndent()
            val data = CreateNewTicketRequest(subject=subject,body=description,priority="", category = "")
            ContainerActivity.OpenContainer(requireContext(),"NewTicketFragment",data)
        }

        setTransactionStatus(data)
    }

    private fun setTransactionStatus(data: Data) {
        val context = requireContext()
        val binding = _binding ?: return
        val statusKey = data.status.lowercase()
        val status = when (statusKey) {
            "pending" -> "Pending"
            "verified" -> "Verified"
            else -> "Rejected"
        }

        val message = when (statusKey) {
            "pending" -> "Transaction in Pending"
            "verified" -> "Transaction is Verified"
            else -> "Transaction is Rejected"
        }

        val description = when (statusKey) {
            "pending" -> "Commission will be credited to your account"
            "verified" -> "Cashback is credited to your account"
            else -> ""
        }

        val estimatedPayoutTime = when (statusKey) {
            "pending" -> "60 Days from Order date"
            "verified" -> "Cashback Credited"
            else -> "Cashback Rejected"
        }

        val statusColorRes = when (statusKey) {
            "pending" -> R.color.orange
            "verified" -> R.color.green
            else -> R.color.red
        }

        val backgroundColorRes = when (statusKey) {
            "pending" -> R.color.status_note_stroke
            "verified" -> R.color.status_verified_bg
            else -> R.color.red_100
        }

        val statusImg = when (statusKey) {
            "pending" -> R.drawable.outline_history_24
            "verified" -> R.drawable.baseline_check_circle_24
            else -> R.drawable.baseline_cancel_24
        }

        val statusColor = ContextCompat.getColor(context, statusColorRes)
        val backgroundColor = ContextCompat.getColor(context, backgroundColorRes)

        with(binding) {
            transactionStatus.text = status
            topStatusText.text = status
            estimatedPayout.text = estimatedPayoutTime
            progressStatus.text = status
            statusDescription.text = description
            progressDate.text = ""
            this.status.text = message
            transactionStatus.setTextColor(statusColor)
            progressStatus.setTextColor(statusColor)
            topStatusText.setTextColor(statusColor)
            icon.setColorFilter(statusColor)
            topStatusIcon.setColorFilter(statusColor)
            statusView.setCardBackgroundColor(backgroundColor)
            topStatusView.setCardBackgroundColor(backgroundColor)
            step3Icon.setCardBackgroundColor(backgroundColor)
            Glide.with(context).load(statusImg).into(topStatusIcon)
            Glide.with(context).load(statusImg).into(icon)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TransactionDetailFragment().apply {
            }
    }
}